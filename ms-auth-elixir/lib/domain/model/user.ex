defmodule Authelixir.Domain.Model.User do
  @moduledoc """
  User
  """

  defstruct [:email, :password, :created_at]

  @type t :: %__MODULE__{
          email: String.t(),
          password: String.t(),
          created_at: DateTime.t()
        }

  @email_regex ~r/^[^@\s]+@[^@\s]+\.[^@\s]+$/

  @spec build(String.t(), String.t()) ::
          {:ok, t()} | {:error, Authelixir.Domain.Model.AppException.t()}
  def build(email, password) do
    with :ok <- validate_email(email),
         :ok <- validate_password(password) do
      {:ok,
       %__MODULE__{
         email: String.downcase(email),
         password: password,
         created_at: DateTime.utc_now()
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

  defp error(code, msg),
    do: {:error, Authelixir.Domain.Model.AppException.new(code, msg)}
end
