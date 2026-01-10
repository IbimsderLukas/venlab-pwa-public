<template>
  <v-container class="fill-height" fluid>
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card elevation="12">
          <v-toolbar color="primary">
            <v-toolbar-title>VenLab - Anmeldung</v-toolbar-title>
            <v-spacer />
            <ThemeSwitch />
          </v-toolbar>

          <v-card-text class="pt-6">
            <v-form @submit.prevent="handleLogin">
              <v-text-field
                v-model="username"
                label="Benutzername"
                prepend-icon="mdi-account"
                :disabled="auth.isLoading"
                autocomplete="username"
              />
              
              <v-text-field
                v-model="password"
                label="Passwort"
                prepend-icon="mdi-lock"
                :type="showPassword ? 'text' : 'password'"
                :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append="showPassword = !showPassword"
                :disabled="auth.isLoading"
                autocomplete="current-password"
              />

              <v-alert v-if="auth.error" type="error" density="compact" class="mb-4">
                {{ auth.error }}
              </v-alert>

              <v-btn
                type="submit"
                color="primary"
                block
                size="large"
                :loading="auth.isLoading"
                class="mt-4"
              >
                Anmelden
              </v-btn>
            </v-form>
          </v-card-text>

          <v-divider />

          <v-card-text class="text-center text-caption">
            <strong>Demo-Zugänge:</strong><br>
            admin / admin123 | user / user123 | researcher / research123
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
const showPassword = ref(false)

async function handleLogin() {
  const success = await auth.login(username.value, password.value)
  if (success) {
    router.push('/')
  }
}
</script>

<style scoped>
.fill-height {
  min-height: 100vh;
}
</style>
