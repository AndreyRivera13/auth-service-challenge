defmodule Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.Signinadapter.SignInAdapterImpl do
  @moduledoc "Adaptador en memoria para guardar sesiones (SignIn)."

  @behaviour Authelixir.Domain.Behaviours.SignInBehaviour

  alias Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.Shared.MemoryStore
  alias Authelixir.Domain.Model.Bancolombia.Session.Session

  @impl true
  @spec save(Session.t()) :: {:ok, Session.t()} | {:error, term()}
  def save(%Session{} = session) do
    case MemoryStore.put_session(session) do
      {:ok, %Session{} = s} -> {:ok, s}
      other -> {:error, other}
    end
  end
end
