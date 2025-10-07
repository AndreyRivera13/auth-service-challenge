defmodule Authelixir.Domain.UseCases.Bancolombia.Signin.Signin do
  @moduledoc """
  Caso de uso: Signin

  Reglas:
  - Buscar usuario por email.
  - Si no existe -> USER_NOT_FOUND.
  - Comparar password texto plano (NO hacer hash aquí).
  - Si no coincide -> INVALID_CREDENTIALS.
  - Crear session_id (UUID) y persistir sesión en memoria.
  - Retornar {:ok, session} sin exponer password.
  - Errores se retornan como {:error, %AppException{}} (un solo tipo de error de dominio).
  """

  alias Authelixir.Domain.Model.{AppException, Session, User, ContextData}

  @type context :: ContextData.t()
  @type email :: String.t()
  @type password :: String.t()
  @type user_repo :: module()
  @type session_repo :: module()

  @spec execute(context, email, password, user_repo, session_repo) ::
          {:ok, Session.t()} | {:error, AppException.t()}
  def execute(%ContextData{} = ctx, email, password, user_repo, session_repo) do
    with {:ok, %User{} = user} <- fetch_user(ctx, email, user_repo),
         :ok <- validate_password(user, password),
         {:ok, %Session{} = session} <- create_and_store_session(ctx, user, session_repo) do
      {:ok, session}
    else
      {:error, %AppException{} = e} -> {:error, e}
      {:error, reason} when is_atom(reason) -> {:error, AppException.new(reason)}
    end
  rescue
    e ->
      {:error, AppException.new(:UNEXPECTED_ERROR, "Unexpected error: #{inspect(e.__struct__)}")}
  end

  # ---- Privadas ----

  defp fetch_user(ctx, email, repo) do
    case repo.get_by_email(ctx, email) do
      {:ok, %User{} = u} -> {:ok, u}
      {:error, :USER_NOT_FOUND} -> {:error, AppException.new(:USER_NOT_FOUND, "User not found")}
      {:error, %AppException{} = e} -> {:error, e}
      _ -> {:error, AppException.new(:USER_NOT_FOUND, "User not found")}
    end
  end

  defp validate_password(%User{password: stored}, given) when is_binary(given) do
    if stored == given do
      :ok
    else
      {:error, AppException.new(:INVALID_CREDENTIALS, "Invalid credentials")}
    end
  end

  defp validate_password(_user, _pw),
    do: {:error, AppException.new(:INVALID_CREDENTIALS, "Invalid credentials")}

  defp create_and_store_session(ctx, %User{email: email}, repo) do
    session = Session.new(email)

    case repo.save(ctx, session) do
      {:ok, %Session{} = s} -> {:ok, s}
      {:error, %AppException{} = e} -> {:error, e}
      {:error, reason} when is_atom(reason) -> {:error, AppException.new(reason)}
      _ -> {:error, AppException.new(:UNEXPECTED_ERROR, "Cannot persist session")}
    end
  end
end
