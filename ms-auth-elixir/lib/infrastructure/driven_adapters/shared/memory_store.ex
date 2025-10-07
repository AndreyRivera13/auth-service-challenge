defmodule Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.Shared.MemoryStore do
  @moduledoc "Store en memoria (ETS) para usuarios y sesiones."

  alias Authelixir.Domain.Model.Bancolombia.User.User
  alias Authelixir.Domain.Model.Bancolombia.Session.Session

  @users_table :ms_auth_users
  @sessions_table :ms_auth_sessions

  @doc """
  Crea (si no existen) las ETS y queda el proceso llamante como dueño.
  Debe invocarse desde un proceso supervisado y de vida larga.
  """
  @spec init_tables() :: :ok
  def init_tables do
    create_if_needed(@users_table)
    create_if_needed(@sessions_table)
    :ok
  end

  # Usuarios

  @spec put_user(User.t()) :: {:ok, User.t()} | {:error, :EMAIL_ALREADY_EXISTS}
  def put_user(%User{email: email} = user) when is_binary(email) do
    ensure_tables()
    key = String.downcase(email)

    case :ets.lookup(@users_table, key) do
      [] ->
        true = :ets.insert(@users_table, {key, user})
        {:ok, user}

      _ ->
        {:error, :EMAIL_ALREADY_EXISTS}
    end
  end

  @spec get_user(String.t()) :: {:ok, User.t()} | :not_found
  def get_user(email) when is_binary(email) do
    ensure_tables()
    key = String.downcase(email)

    case :ets.lookup(@users_table, key) do
      [{^key, user}] -> {:ok, user}
      [] -> :not_found
    end
  end

  # Sesiones

  @spec put_session(Session.t()) :: {:ok, Session.t()}
  def put_session(%Session{session_id: id} = session) when is_binary(id) do
    ensure_tables()
    true = :ets.insert(@sessions_table, {id, session})
    {:ok, session}
  end

  @spec get_session(String.t()) :: {:ok, Session.t()} | :not_found
  def get_session(id) when is_binary(id) do
    ensure_tables()

    case :ets.lookup(@sessions_table, id) do
      [{^id, session}] -> {:ok, session}
      [] -> :not_found
    end
  end

  # Internos

  defp ensure_tables do
    create_if_needed(@users_table)
    create_if_needed(@sessions_table)
    :ok
  end

  defp create_if_needed(name) do
    case :ets.whereis(name) do
      :undefined -> :ets.new(name, [:set, :public, :named_table, read_concurrency: true, write_concurrency: true])
      _tid -> :ok
    end
  end
end
