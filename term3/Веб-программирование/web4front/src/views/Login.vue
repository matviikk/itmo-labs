<template>
  <div class="custom-container">
    <Header />
    <div class="main-content">
      <div class="flex flex-column align-items-center">
        <div class="login-form">
          <h2 class="text-center mb-4">{{ isRegistering ? 'Register' : 'Login' }}</h2>
          <div class="form-group mb-3">
            <label>Username</label>
            <input
                type="text"
                v-model="username"
                class="form-control"
                :class="{ 'error': errors.username }"
            />
            <span v-if="errors.username" class="error-text">{{ errors.username }}</span>
          </div>
          <div class="form-group mb-3">
            <label>Password</label>
            <input
                type="password"
                v-model="password"
                class="form-control"
                :class="{ 'error': errors.password }"
            />
            <span v-if="errors.password" class="error-text">{{ errors.password }}</span>
          </div>
          <div v-if="isRegistering" class="form-group mb-3">
            <label>Confirm Password</label>
            <input
                type="password"
                v-model="confirmPassword"
                class="form-control"
                :class="{ 'error': errors.confirmPassword }"
            />
            <span v-if="errors.confirmPassword" class="error-text">{{ errors.confirmPassword }}</span>
          </div>
          <div class="flex justify-content-between gap-3">
            <button @click="handleSubmit" class="ui-button-primary custom-button">
              {{ isRegistering ? 'Register' : 'Login' }}
            </button>
            <button @click="toggleMode" class="ui-button-secondary custom-button">
              {{ isRegistering ? 'Back to Login' : 'Register' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Header from '../components/Header.vue'
import axios from 'axios'

export default {
  name: 'Login',
  components: {
    Header
  },
  data() {
    return {
      username: '',
      password: '',
      confirmPassword: '',
      isRegistering: false,
      errors: {
        username: '',
        password: '',
        confirmPassword: ''
      }
    }
  },
  methods: {
    validateForm() {
      this.errors = {
        username: '',
        password: '',
        confirmPassword: ''
      }

      if (!this.username) {
        this.errors.username = 'Username is required'
      }
      if (!this.password) {
        this.errors.password = 'Password is required'
      }
      if (this.isRegistering && this.password !== this.confirmPassword) {
        this.errors.confirmPassword = 'Passwords do not match'
      }

      return !Object.values(this.errors).some(error => error)
    },
    toggleMode() {
      this.isRegistering = !this.isRegistering
      this.username = ''
      this.password = ''
      this.confirmPassword = ''
      this.errors = {
        username: '',
        password: '',
        confirmPassword: ''
      }
    },
    async handleSubmit() {
      if (!this.validateForm()) return

      try {
        if (this.isRegistering) {
          await axios.post('/api/auth/register', {
            username: this.username,
            password: this.password,
            confirmPassword: this.confirmPassword
          })
          this.isRegistering = false
          this.password = ''
          this.confirmPassword = ''
        } else {
          const response = await axios.post('/api/auth/login', {
            username: this.username,
            password: this.password
          })
          localStorage.setItem('token', response.data.token)
          this.$router.push('/main')
        }
      } catch (error) {
        if (error.response?.status === 401) {
          this.errors.password = 'Invalid credentials'
        } else {
          this.errors.username = this.isRegistering ? 'Registration failed' : 'Login failed'
        }
      }
    }
  }
}
</script>

<style scoped>
.form-control {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
  margin-top: 0.25rem;
}

.form-control.error {
  border-color: #dc3545;
}

.error-text {
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}
</style>