<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card elevation="12">
          <v-toolbar color="primary" dark>
            <v-toolbar-title>VenLab - Login</v-toolbar-title>
            <v-spacer />
            <ThemeSwitch />
          </v-toolbar>

          <v-card-text>
            <v-form @submit.prevent="handleLogin">
              <v-text-field
                v-model="username"
                label="Benutzername"
                prepend-icon="mdi-account"
                :disabled="auth.isLoading"
              />
              <v-text-field
                v-model="password"
                label="Passwort"
                prepend-icon="mdi-lock"
                :type="showPw ? 'text' : 'password'"
                :append-icon="showPw ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append="showPw = !showPw"
                :disabled="auth.isLoading"
              />
              <v-alert v-if="auth.error" type="error" density="compact" class="mb-4">
                {{ auth.error }}
              </v-alert>
              <v-btn type="submit" color="primary" block size="large" :loading="auth.isLoading">
                Anmelden
              </v-btn>
            </v-form>
          </v-card-text>
          
          <v-card-text class="text-center text-caption">
            Demo: admin/admin123, user/user123
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import ThemeSwitch from '@/components/ThemeSwitch.vue'

const router = useRouter()
const auth = useAuthStore()

const username = ref('')
const password = ref('')
const showPw = ref(false)

async function handleLogin() {
  const success = await auth.login(username.value, password.value)
  if (success) router.push('/')
}
</script>

<style scoped>
.fill-height { min-height: 100vh; }
</style>
