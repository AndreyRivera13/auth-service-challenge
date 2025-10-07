defmodule Authelixir.Domain.Model.Bancolombia.User.User do
  @moduledoc """
  User
  """

  alias Authelixir.Domain.Model.Bancolombia.Shared.Common.Exception.AppException

  defstruct [:email, :password, :name]

  @type t :: %__MODULE__{
          email: String.t(),
          password: String.t(),
          name: String.t()
        }

  @email_regex ~r/^[^@\s]+@[^@\s]+\.[^@\s]+$/

  @spec build(String.t(), String.t(), String.t()) ::
          {:ok, t()} | {:error, AppException.t()}
  def build(email, password, name) do
    with :ok <- validate_email(email),
         :ok <- validate_password(password),
         :ok <- validate_name(name) do
      {:ok,
       %__MODULE__{
         email: String.downcase(email),
         password: password,
         name: name
       }}
    end
  end

  @spec validate_email(String.t()) :: :ok | {:error, any()}
  def validate_email(e) when is_binary(e) and byte_size(e) > 3 do
    if Regex.match?(@email_regex, e),
      do: :ok,
      else: error(:INVALID_EMAIL_FORMAT, "Invalid email format")
  end

  def validate_email(_), do: error(:INVALID_EMAIL_FORMAT, "Invalid email format")

  @spec validate_password(String.t()) :: :ok | {:error, any()}
  def validate_password(p) when is_binary(p) and byte_size(p) >= 8, do: :ok
  def validate_password(_), do: error(:WEAK_PASSWORD, "Weak password")
  def validate_name(_), do: error(:INVALID_NAME, "Invalid name")

  defp error(code, msg), do: {:error, AppException.new(code, msg)}
end
