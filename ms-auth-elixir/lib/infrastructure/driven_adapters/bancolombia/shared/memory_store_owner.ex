defmodule Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.Shared.MemoryStoreOwner do
  @moduledoc """
  Proceso dueño de las ETS de usuarios y sesiones.
  Debe iniciarse al arrancar la aplicación para que las ETS persistan entre requests.
  """

  use GenServer

  alias Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.Shared.MemoryStore

  def start_link(_opts), do: GenServer.start_link(__MODULE__, :ok, name: __MODULE__)

  @impl true
  def init(:ok) do
    MemoryStore.init_tables()
    {:ok, %{}}
  end
end
