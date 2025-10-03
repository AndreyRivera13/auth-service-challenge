defmodule Authelixir.Domain.Model.Appexception do
  @moduledoc """
  Appexception
  """

  @codes ~w(
    INVALID_EMAIL_FORMAT
    WEAK_PASSWORD
    EMAIL_ALREADY_EXISTS
    USER_NOT_FOUND
    INVALID_CREDENTIALS
    MALFORMED_REQUEST
    UNEXPECTED_ERROR
  )a

  @http_map %{
    INVALID_EMAIL_FORMAT: 400,
    WEAK_PASSWORD: 400,
    MALFORMED_REQUEST: 400,
    INVALID_CREDENTIALS: 401,
    USER_NOT_FOUND: 404,
    EMAIL_ALREADY_EXISTS: 409,
    UNEXPECTED_ERROR: 500
  }

  defstruct [:code, :message, :metadata]

  @type code :: atom()
  @type t :: %__MODULE__{code: code(), message: String.t() | nil, metadata: map() | nil}

  @spec new(code()) :: t()
  def new(code) when code in @codes, do: %__MODULE__{code: code}

  @spec new(code(), String.t() | nil, map() | nil) :: t()
  def new(code, message, metadata \\ nil) when code in @codes do
    %__MODULE__{code: code, message: message, metadata: scrub(metadata)}
  end

  def new(_other, message \\ "Unexpected error", metadata \\ nil),
    do: %__MODULE__{code: :UNEXPECTED_ERROR, message: message, metadata: scrub(metadata)}

  @spec http_status(t()) :: pos_integer()
  def http_status(%__MODULE__{code: c}), do: Map.get(@http_map, c, 500)

  defp scrub(nil), do: nil
  defp scrub(m) when is_map(m) do
    Map.drop(m, [:password, "password"])
  end
end
