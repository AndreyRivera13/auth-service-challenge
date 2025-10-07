defmodule Authelixir.Domain.UseCase.Bancolombia.Signin.Signin do
  @moduledoc "Caso de uso SignIn con modelo Signin (User + Session)."

  alias Authelixir.Domain.Model.Bancolombia.Shared.Common.Exception.AppException
  alias Authelixir.Domain.Model.Bancolombia.Shared.Crqs.Contextdata.ContextData
  alias Authelixir.Domain.Model.Bancolombia.Signin.Signin, as: SigninModel
  alias Authelixir.Domain.Model.Bancolombia.Session.Session
  alias Authelixir.Domain.Model.Bancolombia.User.User

  import Bitwise

  @type context :: ContextData.t()
  @type user_repo :: module()
  @type session_repo :: module()

  @spec execute(context, String.t(), String.t(), user_repo, session_repo) ::
          {:ok, Session.t()} | {:error, AppException.t()}
  def execute(%ContextData{} = ctx, email, password, user_repo, session_repo)
      when is_binary(email) and is_binary(password) do
    model = %SigninModel{user: %User{email: email, password: password}}
    execute(ctx, model, user_repo, session_repo)
  end

  @spec execute(context, SigninModel.t(), user_repo, session_repo) ::
          {:ok, Session.t()} | {:error, AppException.t()}
  def execute(%ContextData{} = _ctx, %SigninModel{user: %User{email: email, password: given_pw}}, user_repo, session_repo) do
    with {:ok, %User{password: stored_pw}} <- fetch_user(user_repo, email),
         :ok <- compare_passwords(stored_pw, given_pw),
         {:ok, %Session{} = session} <- create_and_store_session(session_repo, email) do
      {:ok, session}
    else
      {:error, %AppException{} = e} -> {:error, e}
      :not_found -> {:error, AppException.new(:USER_NOT_FOUND, "User not found", nil)}
      other when is_atom(other) -> {:error, AppException.new(other, nil, nil)}
      _ -> {:error, AppException.new(:UNEXPECTED_ERROR, "Unexpected error", nil)}
    end
  rescue
    _e -> {:error, AppException.new(:UNEXPECTED_ERROR, "Unexpected error", nil)}
  end

  # Privadas

  defp fetch_user(user_repo, email) do
    case user_repo.find_by_email(email) do
      {:ok, %User{} = u} -> {:ok, u}
      :not_found -> :not_found
      {:error, _} -> {:error, AppException.new(:UNEXPECTED_ERROR, "Cannot fetch user", nil)}
    end
  end

  defp compare_passwords(stored, given) when is_binary(given) and stored == given, do: :ok
  defp compare_passwords(_stored, _given), do: {:error, AppException.new(:INVALID_CREDENTIALS, "Invalid credentials", nil)}

  defp create_and_store_session(session_repo, email) do
    session = %Session{session_id: uuid_v4(), email: String.downcase(email)}

    case session_repo.save(session) do
      {:ok, %Session{} = s} -> {:ok, s}
      {:error, _} -> {:error, AppException.new(:UNEXPECTED_ERROR, "Cannot persist session", nil)}
    end
  end

  # UUID v4
  defp uuid_v4 do
    <<a::32, b::16, c::16, d::16, e::48>> = :crypto.strong_rand_bytes(16)
    c = (c &&& 0x0FFF) ||| 0x4000
    d = (d &&& 0x3FFF) ||| 0x8000
    :io_lib.format("~8.16.0b-~4.16.0b-~4.16.0b-~4.16.0b-~12.16.0b", [a, b, c, d, e]) |> IO.iodata_to_binary()
  end
end
