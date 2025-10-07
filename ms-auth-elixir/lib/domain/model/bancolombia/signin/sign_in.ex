defmodule Authelixir.Domain.Model.Bancolombia.Signin.Signin do
  @moduledoc "Modelo de entrada para SignIn. Envoltorio de User y Session."

  alias Authelixir.Domain.Model.Bancolombia.User.User
  alias Authelixir.Domain.Model.Bancolombia.Session.Session

  defstruct [:user, :session]

  @type t :: %__MODULE__{
          user: User.t(),
          session: Session.t() | nil
        }
end
