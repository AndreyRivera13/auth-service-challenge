defmodule Authelixir.Domain.Behaviours.SignUpBehaviour do
  @moduledoc """
  SignUpBehaviour
  """

  alias Authelixir.Domain.Model.Bancolombia.User.User

  @callback save(User.t()) :: {:ok, User.t()} | {:error, term()}
end
