defmodule Authelixir.Domain.Behaviours.SignInBehaviour do
  @moduledoc """
  SignInBehaviour
  """

  @callback save(Session.t()) :: {:ok, Session.t()} | {:error, term()}

end
