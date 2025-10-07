defmodule Authelixir.Domain.UseCases.Bancolombia.Signup.Signup do
  @moduledoc "Caso de uso SignUp con modelo Signup (envuelve User)."

  alias Authelixir.Domain.Model.Bancolombia.Exception.AppException
  alias Authelixir.Domain.Model.Bancolombia.Contextdata.ContextData
  alias Authelixir.Domain.Model.Bancolombia.Signup.Signup, as: SignupModel
  alias Authelixir.Domain.Model.Bancolombia.User.User

  @email_regex ~r/^[^@\s]+@[^@\s]+\.[^@\s]+$/

  @type context :: ContextData.t()
  @type user_repo :: module()
  @type signup_repo :: module()

  @spec execute(context, SignupModel.t(), user_repo, signup_repo) ::
          {:ok, :created} | {:error, AppException.t()}
  def execute(%ContextData{} = _ctx, %SignupModel{user: %User{email: email, password: password}}, user_repo, signup_repo) do
    with :ok <- validate_email(email),
         :ok <- validate_password(password),
         :ok <- ensure_unique(user_repo, email),
         {:ok, _saved} <- persist(signup_repo, %User{email: String.downcase(email), password: password, name: nil}) do
      {:ok, :created}
    else
      {:error, %AppException{} = e} -> {:error, e}
      {:error, reason} when is_atom(reason) -> {:error, AppException.new(reason, nil, nil)}
      other -> {:error, AppException.new(:UNEXPECTED_ERROR, "Unexpected: #{inspect(other)}", nil)}
    end
  rescue
    _e -> {:error, AppException.new(:UNEXPECTED_ERROR, "Unexpected error", nil)}
  end

  # Privadas

  defp ensure_unique(user_repo, email) do
    case user_repo.find_by_email(email) do
      :not_found -> :ok
      {:ok, _user} -> {:error, AppException.new(:EMAIL_ALREADY_EXISTS, "Email already exists", nil)}
      {:error, _} -> {:error, AppException.new(:UNEXPECTED_ERROR, "Cannot verify user uniqueness", nil)}
    end
  end

  defp persist(signup_repo, %User{} = user) do
    case signup_repo.save(user) do
      {:ok, %User{}} -> {:ok, :created}
      {:error, :EMAIL_ALREADY_EXISTS} -> {:error, AppException.new(:EMAIL_ALREADY_EXISTS, "Email already exists", nil)}
      {:error, _} -> {:error, AppException.new(:UNEXPECTED_ERROR, "Cannot persist user", nil)}
    end
  end

  defp validate_email(e) when is_binary(e) and byte_size(e) > 3 do
    if Regex.match?(@email_regex, e), do: :ok, else: {:error, AppException.new(:INVALID_EMAIL_FORMAT, "Invalid email format", nil)}
  end

  defp validate_email(_), do: {:error, AppException.new(:INVALID_EMAIL_FORMAT, "Invalid email format", nil)}

  defp validate_password(p) when is_binary(p) and byte_size(p) >= 8, do: :ok
  defp validate_password(_), do: {:error, AppException.new(:WEAK_PASSWORD, "Weak password", nil)}
end
