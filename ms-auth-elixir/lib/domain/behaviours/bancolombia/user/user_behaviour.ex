defmodule Authelixir.Domain.Behaviours.UserBehaviour do

 @moduledoc """
  UserBehaviour
  Puerto de dominio para persistencia de usuarios.
  """

  alias Authelixir.Domain.Model.User

  @callback find_by_email(String.t()) :: {:ok, User.t()} | :not_found | {:error, term()}
end
