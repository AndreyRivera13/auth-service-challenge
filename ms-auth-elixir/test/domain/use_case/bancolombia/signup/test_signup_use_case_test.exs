defmodule TestSignupUseCase do
  use ExUnit.Case, async: true

  alias Authelixir.Domain.UseCase.Bancolombia.Signup.Signup
  alias Authelixir.Domain.Model.Bancolombia.Shared.Crqs.Contextdata.ContextData
  alias Authelixir.Domain.Model.Bancolombia.Shared.Common.Exception.AppException
  alias Authelixir.Domain.Model.Bancolombia.User.User
  alias Authelixir.Domain.Model.Bancolombia.Signup.Signup, as: SignupModel

  # Fakes
  defmodule FakeUserRepoEmpty do
    @behaviour Authelixir.Domain.Behaviours.UserBehaviour
    @impl true
    def find_by_email(_email), do: :not_found
  end

  defmodule FakeUserRepoExisting do
    @behaviour Authelixir.Domain.Behaviours.UserBehaviour
    @impl true
    def find_by_email(email), do: {:ok, %User{email: email, password: "any", name: "Existing"}}
  end

  defmodule FakeSignupRepoOK do
    @behaviour Authelixir.Domain.Behaviours.SignUpBehaviour
    @impl true
    def save(%User{} = user), do: {:ok, user}
  end

  defmodule FakeSignupRepoAlreadyExists do
    @behaviour Authelixir.Domain.Behaviours.SignUpBehaviour
    @impl true
    def save(_user), do: {:error, :EMAIL_ALREADY_EXISTS}
  end

  defp ctx, do: ContextData.new("00000000-0000-4000-8000-000000000000", "00000000-0000-4000-8000-000000000001")

  defp assert_error_code({:error, %AppException{code: code}}, expected), do: assert(code == expected)
  defp assert_error_code(other, _expected), do: flunk("Esperado {:error, %AppException{}}, recibido: #{inspect(other)}")

  describe "Signup.execute/5 (primitivos)" do
    test "OK cuando email es nuevo y password fuerte" do
      result = Signup.execute(ctx(), "john.doe@example.com", "StrongPass1", FakeUserRepoEmpty, FakeSignupRepoOK)
      assert result == {:ok, :created}
    end

    test "error por email inválido" do
      result = Signup.execute(ctx(), "bad", "StrongPass1", FakeUserRepoEmpty, FakeSignupRepoOK)
      assert_error_code(result, :INVALID_EMAIL_FORMAT)
    end

    test "error por password débil" do
      result = Signup.execute(ctx(), "jane@example.com", "123", FakeUserRepoEmpty, FakeSignupRepoOK)
      assert_error_code(result, :WEAK_PASSWORD)
    end

    test "error por email ya existente (verificación de unicidad)" do
      result = Signup.execute(ctx(), "existing@example.com", "StrongPass1", FakeUserRepoExisting, FakeSignupRepoOK)
      assert_error_code(result, :EMAIL_ALREADY_EXISTS)
    end

    test "error por conflicto al persistir" do
      result = Signup.execute(ctx(), "dupe@example.com", "StrongPass1", FakeUserRepoEmpty, FakeSignupRepoAlreadyExists)
      assert_error_code(result, :EMAIL_ALREADY_EXISTS)
    end
  end

  describe "Signup.execute/4 (modelo)" do
    test "OK con modelo válido" do
      model = %SignupModel{user: %User{email: "ok@example.com", password: "StrongPass1", name: "Ok"}}
      result = Signup.execute(ctx(), model, FakeUserRepoEmpty, FakeSignupRepoOK)
      assert result == {:ok, :created}
    end
  end
end