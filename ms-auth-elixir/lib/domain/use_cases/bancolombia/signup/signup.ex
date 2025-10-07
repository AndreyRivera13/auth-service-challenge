defmodule Authelixir.Domain.UseCases.Bancolombia.Signup.Signup do
  @moduledoc """
  Signup
    Reglas:
  - Validar email y password (≥ 8).
  - Email debe ser único.
  - Persistencia en memoria a través del repo inyectado.
  - Retorna {:ok, :created} sin exponer password.
  - Errores se retornan como {:error, %AppException{}}.
  """

  alias Authelixir.Domain.Model.Bancolombia.Exception.AppException
  alias Authelixir.Domain.Model.Bancolombia.User.User
  alias Authelixir.Domain.Model.Bancolombia.Contextdata.ContextData

  @type context :: ContextData.t()
  @type email :: String.t()
  @type password :: String.t()
  @type user_repo :: module()

  @spec execute(context, email, password, user_repo) ::
          {:ok, :created} | {:error, AppException.t()}
  def execute(%ContextData{} = ctx, email, password, user_repo) do
    with {:ok, %User{} = user} <- User.build(email, password),
         :ok <- ensure_unique(ctx, user.email, user_repo),
         {:ok, _saved} <- persist(ctx, user, user_repo) do
      {:ok, :created}
    else
      {:error, %AppException{} = e} -> {:error, e}
      {:error, reason} when is_atom(reason) -> {:error, AppException.new(reason)}
    end
  rescue
    e ->
      {:error, AppException.new(:UNEXPECTED_ERROR, "Unexpected error: #{inspect(e.__struct__)}")}
  end

  # ...existing code...

  # Privadas

  defp ensure_unique(ctx, email, repo) do
    case repo.get_by_email(ctx, email) do
      {:error, :USER_NOT_FOUND} ->
        :ok

      {:ok, _user} ->
        {:error, AppException.new(:EMAIL_ALREADY_EXISTS, "Email already exists")}

      {:error, %AppException{} = e} ->
        {:error, e}

      other ->
        # Cualquier respuesta inesperada del adaptador
        _ = other
        {:error, AppException.new(:UNEXPECTED_ERROR, "Cannot verify user uniqueness")}
    end
  end

  defp persist(ctx, %User{} = user, repo) do
    case repo.save(ctx, user) do
      {:ok, %User{} = saved} -> {:ok, saved}
      {:error, %AppException{} = e} -> {:error, e}
      {:error, reason} when is_atom(reason) -> {:error, AppException.new(reason)}
      _ -> {:error, AppException.new(:UNEXPECTED_ERROR, "Cannot persist user")}
    end
  end
end
