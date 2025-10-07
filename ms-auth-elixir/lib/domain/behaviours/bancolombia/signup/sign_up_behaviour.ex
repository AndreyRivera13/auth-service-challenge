defmodule Authelixir.Domain.Behaviours.SignUpBehaviour do
  @moduledoc """
  SignUpBehaviour
  """

  # @callback replace_function_name(param_one::term, param_two::term)::{:ok, true::term} | {:error, reason::term}
    @callback save(User.t()) :: {:ok, User.t()} | {:error, term()}
end
