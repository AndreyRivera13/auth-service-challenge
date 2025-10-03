defmodule Authelixir.Infrastructure.EntryPoint.HealthCheck do
  @moduledoc """
  Authelixir health check
  """

  def checks do
    [
      %PlugCheckup.Check{name: "http", module: __MODULE__, function: :check_http}
    ]
  end

  def check_http do
    :ok
  end
end
