defmodule Authelixir.Infrastructure.EntryPoints.Bancolombia.HealthCheckTest do
  alias Authelixir.Infrastructure.EntryPoint.Bancolombia.HealthCheck

  use ExUnit.Case

  describe "check_http/0" do
    test "returns :ok" do
      assert HealthCheck.check_http() == :ok
    end
  end
end
