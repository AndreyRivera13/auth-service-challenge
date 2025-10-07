defmodule Authelixir.Domain.Model.Bancolombia.Signup.Signup do
  @moduledoc "Modelo de entrada para SignUp. Envoltorio de User."

  alias Authelixir.Domain.Model.Bancolombia.User.User

  defstruct [:user]

  @type t :: %__MODULE__{
          user: User.t()
        }
end
