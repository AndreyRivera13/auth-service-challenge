defmodule Authelixir.Infrastructure.EntryPoint.Bancolombia.ApiRest do
  @moduledoc """
  API REST con Plug.Router
  - POST /api/v1/signup -> 201 sin body
  - POST /api/v1/signin -> 200 {"session_id": "uuid"}
  Manejo de errores canónico y propagación de headers de trazabilidad.
  """

  use Plug.Router
  require Logger

  alias Authelixir.Domain.Model.Bancolombia.Shared.Common.Exception.AppException
  alias Authelixir.Domain.Model.Bancolombia.Shared.Crqs.Contextdata.ContextData
  alias Authelixir.Domain.UseCase.Bancolombia.Signup.Signup
  alias Authelixir.Domain.UseCase.Bancolombia.Signin.Signin
  alias Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.Useradapter.UserAdapterImpl, as: UserRepo
  alias Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.SignupAdapter.SignUpAdapterImpl, as: SignupRepo
  alias Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.Signinadapter.SignInAdapterImpl, as: SessionRepo

  plug Plug.Logger, log: :info
  plug CORSPlug, origin: ["*"]

  plug Plug.Parsers,
    parsers: [:json],
    pass: ["application/json"],
    json_decoder: Jason

  plug :match
  plug :dispatch

  get "/health" do
    send_resp(conn, 200, "OK")
  end

  post "/api/v1/signup" do
    with {:ok, ctx} <- context_from(conn),
         :ok <- validate_headers_present(ctx),
         {:ok, params} <- strict_json(conn, ~w(email password name), ~w(email password)),
         email when is_binary(email) <- params["email"],
         password when is_binary(password) <- params["password"] do
      case Signup.execute(ctx, email, password, UserRepo, SignupRepo) do
        {:ok, :created} ->
          mid = ctx.message_id
          xid = ctx.x_request_id

          conn
          |> put_resp_header("message-id", mid)
          |> put_resp_header("x-request-id", xid)
          |> send_resp(201, "")

        {:error, %AppException{} = e} ->
          send_error(conn, e)
      end
    else
      {:error, %AppException{} = e} ->
        send_error(conn, e)

      _ ->
        e = AppException.new(:MALFORMED_REQUEST, "Request inválido")
        send_error(conn, e)
    end
  end

  post "/api/v1/signin" do
    with {:ok, ctx} <- context_from(conn),
         :ok <- validate_headers_present(ctx),
         {:ok, params} <- strict_json(conn, ~w(email password), ~w(email password)),
         email when is_binary(email) <- params["email"],
         password when is_binary(password) <- params["password"] do
      case Signin.execute(ctx, email, password, UserRepo, SessionRepo) do
        {:ok, session} ->
          body = Jason.encode!(%{"session_id" => session.session_id})
          mid = ctx.message_id
          xid = ctx.x_request_id
          conn
          |> put_resp_header("message-id", mid)
          |> put_resp_header("x-request-id", xid)
          |> put_resp_content_type("application/json")
          |> send_resp(200, body)

        {:error, %AppException{} = e} ->
          send_error(conn, e)
      end
    else
      {:error, %AppException{} = e} ->
        send_error(conn, e)

      _ ->
        e = AppException.new(:MALFORMED_REQUEST, "Request inválido")
        send_error(conn, e)
    end
  end

  match _ do
    send_resp(conn, 404, "Not found")
  end

  # Helpers

  defp context_from(conn) do
    mid = get_req_header(conn, "message-id") |> List.first()
    xhdr = get_req_header(conn, "x-request-id") |> List.first()
    xid = if is_binary(xhdr) and byte_size(xhdr) > 0, do: xhdr, else: mid
    {:ok, ContextData.new(mid, xid)}
  end

  defp validate_headers_present(ctx) do
    mid = ctx.message_id
    xid = ctx.x_request_id

    if present?(mid) and present?(xid) and uuid?(mid) and uuid?(xid), do: :ok, else: {:error, AppException.new(:MALFORMED_REQUEST, "Headers de trazabilidad inválidos")}
  end

  # Rechaza claves no permitidas y exige campos requeridos
  defp strict_json(conn, allowed_keys, required_keys) do
    case conn.body_params do
      %{} = params ->
        keys = Map.keys(params)
        unknown = Enum.filter(keys, fn k -> not (k in allowed_keys) end)
        cond do
          unknown != [] ->
            {:error, AppException.new(:MALFORMED_REQUEST, "Campos no permitidos: #{Enum.join(unknown, ", ")}")}
          Enum.any?(required_keys, fn k -> Map.get(params, k) in [nil, ""] end) ->
            {:error, AppException.new(:MALFORMED_REQUEST, "Campos obligatorios faltantes")}
          true ->
            {:ok, Map.take(params, allowed_keys)}
        end
      _ ->
        {:error, AppException.new(:MALFORMED_REQUEST, "JSON inválido")}
    end
  end

  defp present?(v), do: is_binary(v) and byte_size(v) > 0

  @uuid_regex ~r/^[0-9a-fA-F]{8}\-[0-9a-fA-F]{4}\-[1-5][0-9a-fA-F]{3}\-[89abAB][0-9a-fA-F]{3}\-[0-9a-fA-F]{12}$/
  defp uuid?(nil), do: false
  defp uuid?(v) when is_binary(v), do: Regex.match?(@uuid_regex, v)

  defp send_error(conn, %AppException{} = e) do
    status = AppException.http_status(e)
    msg_id = get_req_header(conn, "message-id") |> List.first()
    req_id = get_req_header(conn, "x-request-id") |> List.first()
    xid = if is_binary(req_id) and byte_size(req_id) > 0, do: req_id, else: msg_id
    body =
      %{
        "error" => %{
          "code" => e.code |> to_string(),
          "message" => e.message,
          "details" => Map.get(e, :metadata, %{}),
          "correlation" => %{
            "message_id" => msg_id,
            "x_request_id" => xid
          }
        }
      }
      |> Jason.encode!()
    conn
    |> put_resp_header("message-id", msg_id || "")
    |> put_resp_header("x-request-id", xid || "")
    |> put_resp_content_type("application/json")
    |> send_resp(status, body)
  end
end
