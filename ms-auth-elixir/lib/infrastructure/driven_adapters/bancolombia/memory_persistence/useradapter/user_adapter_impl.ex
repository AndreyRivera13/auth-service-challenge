defmodule Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.Useradapter.UserAdapterImpl do
  @moduledoc "Adaptador en memoria para consultar usuarios por email."

  @behaviour Authelixir.Domain.Behaviours.UserBehaviour

  alias Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.Shared.MemoryStore
  alias Authelixir.Domain.Model.Bancolombia.User.User

  @impl true
  @spec find_by_email(String.t()) :: {:ok, User.t()} | :not_found | {:error, term()}
  def find_by_email(email) when is_binary(email) do
    case MemoryStore.get_user(email) do
      {:ok, %User{} = u} -> {:ok, u}
      :not_found -> :not_found
      other -> {:error, other}
    end
  end
end
