defmodule Authelixir.Domain.Model.Session do
  @moduledoc """
  Session
  """

  defstruct [:session_id, :email, :created_at]

  @type t :: %__MODULE__{
          session_id: String.t(),
          email: String.t(),
          created_at: DateTime.t()
        }

  @spec new(String.t()) :: t()
  def new(email) do
    %__MODULE__{
      session_id: uuid(),
      email: String.downcase(email),
      created_at: DateTime.utc_now()
    }
  end

  defp uuid do
    # UUID v4 simple (hex) suficiente para propósito en memoria
    <<a::32, b::16, c::16, d::16, e::48>> = :crypto.strong_rand_bytes(16)
    versioned = Bitwise.bor(Bitwise.band(c, 0x0FFF), 0x4000)
    variant = Bitwise.bor(Bitwise.band(d, 0x3FFF), 0x8000)

    :io_lib.format("~8.16.0b-~4.16.0b-~4.16.0b-~4.16.0b-~12.16.0b", [a, b, versioned, variant, e])
    |> IO.iodata_to_binary()
  end
end
