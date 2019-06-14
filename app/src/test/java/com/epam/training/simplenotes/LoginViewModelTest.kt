package com.epam.training.simplenotes

import android.arch.lifecycle.Observer
import com.epam.training.simplenotes.model.LoginModel
import com.epam.training.simplenotes.viewmodel.LoginViewModel
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class LoginViewModelTest : Spek({
    emulateInstantTaskExecutorRule()

    val authStateObserver: Observer<Boolean> = mockk(relaxed = true)

    val loginModelMock: LoginModel = mockk(relaxed = true)

    val testLoginViewModel = LoginViewModel(
        loginModelMock
    )

    describe("authentication check") {
        beforeEachTest {
            clearMocks(loginModelMock)
        }

        context("user authenticated") {
            beforeEachTest {
                every { loginModelMock.isUserAuthenticated() } returns true
            }

            it("isUserAuthenticated() should return 'true'") {
                assertTrue(testLoginViewModel.isUserAuthenticated())
            }
        }

        context("user not authenticated") {
            beforeEachTest {
                every { loginModelMock.isUserAuthenticated() } returns false
            }

            it("isUserAuthenticated() should return 'false'") {
                assertFalse(testLoginViewModel.isUserAuthenticated())
            }
        }
    }

    describe("signing in check") {
        beforeEachTest {
            clearMocks(loginModelMock)
            testLoginViewModel.authState.observeForever(authStateObserver)
        }

        context("sign in: success scenario") {
            beforeEachTest {
                every { loginModelMock.signIn(any(), any(), any(), any()) } answers {
                    (this.arg<() -> Unit>(2)).invoke()
                }

                testLoginViewModel.signIn("", "")
            }

            it("should sign in") {
                verify { authStateObserver.onChanged(true) }
            }
        }

        context("sign in: error scenario") {
            beforeEachTest {
                every { loginModelMock.signIn(any(), any(), any(), any()) } answers {
                    (this.arg<() -> Unit>(3)).invoke()
                }

                testLoginViewModel.signIn("", "")
            }

            it("should not sign in") {
                verify { authStateObserver.onChanged(false) }
            }
        }

        context("sign up: success scenario") {
            beforeEachTest {
                every { loginModelMock.registerAccount(any(), any(), any(), any()) } answers {
                    (this.arg<() -> Unit>(2)).invoke()
                }

                testLoginViewModel.signIn("", "")
            }

            it("should register account and sign in") {
                verify { authStateObserver.onChanged(true) }
            }
        }

        context("sign up: error scenario") {
            beforeEachTest {
                every { loginModelMock.registerAccount(any(), any(), any(), any()) } answers {
                    (this.arg<() -> Unit>(3)).invoke()
                }

                testLoginViewModel.signIn("", "")
            }

            it("should fail registration") {
                verify { authStateObserver.onChanged(false) }
            }
        }

        afterEachTest {
            testLoginViewModel.authState.removeObserver(authStateObserver)
        }
    }

})