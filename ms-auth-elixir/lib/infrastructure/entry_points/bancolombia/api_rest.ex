defmodule Authelixir.Infrastructure.EntryPoint.Bancolombia.ApiRest do
  @moduledoc """
  API REST con Plug.Router
  - POST /api/v1/signup -> 201 sin body
  - POST /api/v1/signin -> 200 {"session_id": "uuid"}
  Manejo de errores canónico y propagación de headers de trazabilidad.
  """

  use Plug.Router
  require Logger

  alias Authelixir.Domain.Model.Bancolombia.Exception.AppException
  alias Authelixir.Domain.Model.Bancolombia.Contextdata.ContextData
  alias Authelixir.Domain.UseCase.Bancolombia.Signup.Signup
  alias Authelixir.Domain.UseCase.Bancolombia.Signin.Signin
  alias Authelixir.Infrastructure.Repository.{InMemoryUserRepository, InMemorySessionRepository}

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
         {:ok, params} <- strict_json(conn, ~w(email password)),
         email when is_binary(email) <- params["email"],
         password when is_binary(password) <- params["password"] do
      case Signup.execute(ctx, email, password, InMemoryUserRepository) do
        {:ok, :created} ->
          mid = Map.get(ctx, :message_id, "")
          xid = Map.get(ctx, :x_request_id) || Map.get(ctx, :request_id) || ""

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
         {:ok, params} <- strict_json(conn, ~w(email password)),
         email when is_binary(email) <- params["email"],
         password when is_binary(password) <- params["password"] do
      case Signin.execute(ctx, email, password, InMemoryUserRepository, InMemorySessionRepository) do
        {:ok, session} ->
          body = Jason.encode!(%{"session_id" => session.session_id})

          mid = Map.get(ctx, :message_id, "")
          xid = Map.get(ctx, :x_request_id) || Map.get(ctx, :request_id) || ""

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
    message_id = get_req_header(conn, "message-id") |> List.first()
    req_id = get_req_header(conn, "x-request-id") |> List.first()

    # struct/2 ignora claves inexistentes y solo setea las válidas
    ctx =
      struct(ContextData, %{
        message_id: message_id,
        request_id: req_id,
        x_request_id: req_id
      })

    {:ok, ctx}
  end

  defp validate_headers_present(ctx) do
    mid = Map.get(ctx, :message_id)
    xid = Map.get(ctx, :x_request_id) || Map.get(ctx, :request_id)

    if present?(mid) and present?(xid) and uuid?(mid) and uuid?(xid) do
      :ok
    else
      {:error, AppException.new(:MALFORMED_REQUEST, "Headers de trazabilidad inválidos")}
    end
  end

  defp strict_json(conn, allowed_keys) do
    case conn.body_params do
      %{} = params ->
        filtered = Map.take(params, allowed_keys)

        if Enum.any?(allowed_keys, fn k -> Map.get(filtered, k) in [nil, ""] end) do
          {:error, AppException.new(:MALFORMED_REQUEST, "Campos obligatorios faltantes")}
        else
          {:ok, filtered}
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

    msg_id = get_req_header(conn, "message-id") |> List.first() || ""
    req_id = get_req_header(conn, "x-request-id") |> List.first() || ""

    body =
      %{
        "error" => %{
          "code" => e.code |> to_string(),
          "message" => e.message,
          "details" => Map.get(e, :metadata, %{}),
          "correlation" => %{
            "message_id" => msg_id,
            "x_request_id" => req_id
          }
        }
      }
      |> Jason.encode!()

    conn
    |> put_resp_header("message-id", msg_id)
    |> put_resp_header("x-request-id", req_id)
    |> put_resp_content_type("application/json")
    |> send_resp(status, body)
  end
end
