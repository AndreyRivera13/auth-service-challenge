import Config

config :authelixir,
  timezone: "America/Bogota",
  env: :prod,
  http_port: 8080,
  enable_server: true,
  version: "0.0.1",
  custom_metrics_prefix_name: "authelixir"

config :logger,
  level: :warning
