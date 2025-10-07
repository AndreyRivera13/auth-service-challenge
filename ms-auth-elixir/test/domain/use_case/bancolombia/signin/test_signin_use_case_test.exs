defmodule TestSigninUseCase do
  use ExUnit.Case, async: true

  alias Authelixir.Domain.UseCase.Bancolombia.Signin.Signin
  alias Authelixir.Domain.Model.Bancolombia.Shared.Crqs.Contextdata.ContextData
  alias Authelixir.Domain.Model.Bancolombia.Shared.Common.Exception.AppException
  alias Authelixir.Domain.Model.Bancolombia.User.User
  alias Authelixir.Domain.Model.Bancolombia.Session.Session
  alias Authelixir.Domain.Model.Bancolombia.Signin.Signin, as: SigninModel

  # Fakes
  defmodule FakeUserRepoNF do
    @behaviour Authelixir.Domain.Behaviours.UserBehaviour
    @impl true
    def find_by_email(_email), do: :not_found
  end

  defmodule FakeUserRepoOK do
    @behaviour Authelixir.Domain.Behaviours.UserBehaviour
    @impl true
    def find_by_email(email), do: {:ok, %User{email: email, password: "Correct#123", name: "User"}}
  end

  defmodule FakeSessionRepoOK do
    @behaviour Authelixir.Domain.Behaviours.SignInBehaviour
    @impl true
    def save(%Session{} = s), do: {:ok, s}
  end

  defmodule FakeSessionRepoFail do
    @behaviour Authelixir.Domain.Behaviours.SignInBehaviour
    @impl true
    def save(_s), do: {:error, :STORE_DOWN}
  end

  defp ctx, do: ContextData.new("00000000-0000-4000-8000-000000000010", "00000000-0000-4000-8000-000000000011")

  defp assert_error_code({:error, %AppException{code: code}}, expected), do: assert(code == expected)
  defp assert_error_code(other, _expected), do: flunk("Esperado {:error, %AppException{}}, recibido: #{inspect(other)}")

  describe "Signin.execute/5 (primitivos)" do
    test "OK con credenciales válidas" do
      result = Signin.execute(ctx(), "me@example.com", "Correct#123", FakeUserRepoOK, FakeSessionRepoOK)
      assert {:ok, %Session{session_id: id, email: "me@example.com"}} = result
      assert is_binary(id) and byte_size(id) > 0
    end

    test "USER_NOT_FOUND cuando el usuario no existe" do
      result = Signin.execute(ctx(), "nouser@example.com", "any", FakeUserRepoNF, FakeSessionRepoOK)
      assert_error_code(result, :USER_NOT_FOUND)
    end

    test "INVALID_CREDENTIALS cuando el password no coincide" do
      result = Signin.execute(ctx(), "me@example.com", "WrongPass", FakeUserRepoOK, FakeSessionRepoOK)
      assert_error_code(result, :INVALID_CREDENTIALS)
    end

    test "error mapeado cuando falla la persistencia de la sesión" do
      result = Signin.execute(ctx(), "me@example.com", "Correct#123", FakeUserRepoOK, FakeSessionRepoFail)
      assert match?({:error, %AppException{}}, result)
    end
  end

  describe "Signin.execute/4 (modelo)" do
    test "OK con modelo válido" do
      model = %SigninModel{user: %User{email: "ok@example.com", password: "Correct#123"}}
      result = Signin.execute(ctx(), model, FakeUserRepoOK, FakeSessionRepoOK)
      assert {:ok, %Session{email: "ok@example.com"}} = result
    end
  end
end