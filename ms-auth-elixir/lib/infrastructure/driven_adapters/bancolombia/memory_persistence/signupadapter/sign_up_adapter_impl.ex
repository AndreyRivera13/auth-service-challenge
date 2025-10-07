defmodule Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.SignupAdapter.SignUpAdapterImpl do
  @moduledoc "Adaptador en memoria para registrar usuarios (SignUp)."

  @behaviour Authelixir.Domain.Behaviours.SignUpBehaviour

  alias Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.Shared.MemoryStore
  alias Authelixir.Domain.Model.Bancolombia.User.User

  @impl true
  @spec save(User.t()) :: {:ok, User.t()} | {:error, term()}
  def save(%User{} = user) do
    case MemoryStore.put_user(user) do
      {:ok, %User{} = u} -> {:ok, u}
      {:error, :EMAIL_ALREADY_EXISTS} = e -> e
      other -> {:error, other}
    end
  end
end
