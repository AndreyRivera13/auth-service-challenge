defmodule Authelixir.Domain.Model.Bancolombia.ContextData.ContextData do
  @moduledoc """
  ContextData
  """

  defstruct [:message_id, :request_id, :timestamp]

  @type t :: %__MODULE__{
          message_id: String.t(),
          request_id: String.t(),
          timestamp: DateTime.t()
        }

  @spec new(String.t(), String.t()) :: t()
  def new(message_id, request_id) do
    %__MODULE__{
      message_id: message_id,
      request_id: request_id,
      timestamp: DateTime.utc_now()
    }
  end

  @spec put(t(), keyword()) :: t()
  def put(%__MODULE__{} = ctx, kvs) when is_list(kvs) do
    Enum.reduce(kvs, ctx, fn
      {:message_id, v}, acc -> %{acc | message_id: v}
      {:request_id, v}, acc -> %{acc | request_id: v}
      _, acc -> acc
    end)
  end
end
defmodule Authelixir.Domain.Model.ContextData do
  @moduledoc """
  Contexto de trazabilidad que viaja por todas las capas.
  """

  defstruct [:message_id, :x_request_id]

  @type t :: %__MODULE__{
          message_id: String.t(),
          x_request_id: String.t()
        }
end
