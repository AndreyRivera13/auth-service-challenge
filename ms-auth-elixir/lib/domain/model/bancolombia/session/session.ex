defmodule Authelixir.Domain.Model.Bancolombia.Session.Session do
  @moduledoc """
  Sesión de autenticación.
  """

  defstruct [:session_id, :email]

  @type t :: %__MODULE__{
          session_id: String.t(),
          email: String.t()
        }
end
