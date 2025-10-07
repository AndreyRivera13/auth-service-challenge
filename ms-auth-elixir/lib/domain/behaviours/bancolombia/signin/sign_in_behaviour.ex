defmodule Authelixir.Domain.Behaviours.SignInBehaviour do
  @moduledoc """
  SignInBehaviour
  """

  alias Authelixir.Domain.Model.Bancolombia.Session.Session

  @callback save(Session.t()) :: {:ok, Session.t()} | {:error, term()}
end
