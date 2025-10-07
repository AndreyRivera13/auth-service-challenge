defmodule Authelixir.Infrastructure.EntryPoint.Bancolombia.ApiRestTest do
  use ExUnit.Case, async: true

  import Plug.Test
  import Plug.Conn

  alias Authelixir.Infrastructure.EntryPoint.Bancolombia.ApiRest
  alias Authelixir.Infrastructure.Adapters.Bancolombia.MemoryPersistence.Shared.MemoryStore
  alias Authelixir.Domain.Model.Bancolombia.User.User

  @opts ApiRest.init([])
  @uuid "00000000-0000-4000-8000-000000000001"

  setup do
    # Asegura ETS y limpia datos antes de cada test
    MemoryStore.init_tables()
    :ets.delete_all_objects(:ms_auth_users)
    :ets.delete_all_objects(:ms_auth_sessions)
    :ok
  end

  test "GET /api/v1/health => 200" do
    conn =
      :get
      |> conn("/api/v1/health")
      |> ApiRest.call(@opts)

    assert conn.state == :sent
    assert conn.status == 200
    assert conn.resp_body == "OK"
  end

  test "POST /api/v1/signup => 201, setea headers de trazabilidad" do
    body = Jason.encode!(%{"email" => "john.doe@example.com", "password" => "StrongPass1"})
    conn =
      :post
      |> conn("/api/v1/signup", body)
      |> put_req_header("content-type", "application/json")
      |> put_req_header("message-id", @uuid)
      # opcional: sin x-request-id → debe caer al message-id
      |> ApiRest.call(@opts)

    assert conn.state == :sent
    assert conn.status == 201
    assert get_resp_header(conn, "message-id") == [@uuid]
    assert get_resp_header(conn, "x-request-id") == [@uuid]
    assert conn.resp_body == ""
  end

  test "POST /api/v1/signin => 200 con session_id" do
    # Pre-carga usuario en memoria
    {:ok, _} = MemoryStore.put_user(%User{email: "me@example.com", password: "Correct#123", name: "User"})

    body = Jason.encode!(%{"email" => "me@example.com", "password" => "Correct#123"})
    conn =
      :post
      |> conn("/api/v1/signin", body)
      |> put_req_header("content-type", "application/json")
      |> put_req_header("message-id", @uuid)
      |> ApiRest.call(@opts)

    assert conn.state == :sent
    assert conn.status == 200
    assert get_resp_header(conn, "message-id") == [@uuid]
    assert get_resp_header(conn, "x-request-id") == [@uuid]

    assert %{"session_id" => sid} = Jason.decode!(conn.resp_body)
    assert is_binary(sid) and byte_size(sid) > 0
  end

  test "404 para rutas no encontradas" do
    conn =
      :get
      |> conn("/api/v1/unknown")
      |> ApiRest.call(@opts)

    assert conn.state == :sent
    assert conn.status == 404
    assert conn.resp_body == "Not found"
  end
end
