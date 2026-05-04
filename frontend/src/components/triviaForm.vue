<template>
  <div class="trivia-wrapper">

    <!-- left form -->
    <div class="card">
      <div class="card-header">
        <h1>Trivia Generator</h1>
        <p class="subtitle">Customize your quiz</p>
      </div>

      <div class="form-body">
        <!-- Number of Questions -->
        <div class="field-group">
          <label for="amount">Number of Questions</label>
          <input
            id="amount"
            v-model.number="form.amount"
            type="number"
            min="1"
            max="50"
            placeholder="e.g. 10"
            class="text-input"
          />
          <span class="hint">Between 1 and 50</span>
        </div>

        <!-- Select Question Category -->
        <div class="field-group">
          <label for="category">Category</label>
          <div class="select-wrapper">
            <select id="category" v-model="form.category" class="dropdown">
              <option
                v-for="category in options.categories"
                :key="category.value"
                :value="category.value"
              >
                {{ category.label }}
              </option>
            </select>
            <span class="chevron">▾</span>
          </div>
        </div>

        <!-- Select Question Difficulty -->
        <div class="field-group">
          <label for="difficulty">Difficulty</label>
          <div class="select-wrapper">
            <select id="difficulty" v-model="form.difficulty" class="dropdown">
              <option
                v-for="difficulty in options.difficulties"
                :key="difficulty.value"
                :value="difficulty.value"
              >
                {{ difficulty.label }}
              </option>
            </select>
            <span class="chevron">▾</span>
          </div>
        </div>

        <!-- Select Question Type -->
        <div class="field-group">
          <label for="type">Question Type</label>
          <div class="select-wrapper">
            <select id="type" v-model="form.type" class="dropdown">
              <option
                v-for="type in options.types"
                :key="type.value"
                :value="type.value"
              >
                {{ type.label }}
              </option>
            </select>
            <span class="chevron">▾</span>
          </div>
        </div>

        <!-- Error -->
        <p v-if="error" class="error-msg">{{ error }}</p>

        <!-- Submit -->
        <button
          class="submit-btn"
          :class="{ loading: isLoading }"
          :disabled="isLoading"
          @click="fetchTrivia"
        >
          <span v-if="!isLoading">Generate Quiz</span>
          <span v-else class="spinner"></span>
        </button>
      </div>
    </div>

    <!-- Right Result Panel -->
    <div class="results-panel">
      <!-- Empty state -->
      <div v-if="!questions.length && !isLoading" class="empty-state">
        <p>Your questions will appear here</p>
      </div>

      <!-- Loading state -->
      <div v-if="isLoading" class="empty-state">
        <span class="spinner large-spinner"></span>
        <p>Fetching questions...</p>
      </div>

      <!-- Questions -->
      <template v-if="questions.length">
        <div class="results-header">
          <h2>{{ questions.length }} Questions Ready</h2>
        </div>

        <div class="results-scroll">
          <div
            v-for="(q, index) in questions"
            :key="index"
            class="question-card"
          >
            <span class="q-number">{{ index + 1 }}</span>
            <div class="q-content">
              <p class="q-text" v-html="q.question"></p>
              <div class="tags">
                <span class="tag category-tag">{{ q.category }}</span> {{ console.log(q.category) }}
                <span class="tag" :class="`difficulty-${q.difficulty}`">{{ q.difficulty }}</span>
                <span class="tag type-tag">{{ q.type === 'boolean' ? 'True/False' : 'Multiple Choice' }}</span>
              </div>

              <!-- <div class="q-options">
                <ul>
                  <li v-for="(option, idx) in [...q.incorrect_answers, q.correct_answer].sort()" :key="idx">
                    {{ option }}
                  </li>
                </ul>
             </div>  -->

          </div>
        </div>
        </div>  

      </template>

    </div>

  </div>
</template>

<script>
import triviaOptions from '../trivia-options.json'

export default {
  name: 'TriviaForm',

  data() {
    return {
      options: triviaOptions,
      form: {
        amount: 10,
        category: 'any',
        difficulty: 'any',
        type: 'any'
      },
      questions: [],
      isLoading: false,
      error: null
    }
  },

  methods: {
    async fetchTrivia() {
      this.error = null

      if (!this.form.amount || this.form.amount < 1 || this.form.amount > 50) {
        this.error = 'Please enter a number between 1 and 50.'
        return
      }

      this.isLoading = true
      this.questions = []

      try {
        const params = new URLSearchParams({ amount: this.form.amount })

        if (this.form.category !== 'any') params.append('category', this.form.category)
        if (this.form.difficulty !== 'any') params.append('difficulty', this.form.difficulty)
        if (this.form.type !== 'any') params.append('type', this.form.type)

        const url = `https://opentdb.com/api.php?${params.toString()}`
        const response = await fetch(url)
        const data = await response.json()

        const errorMessages = {
        1: 'No results. The API does not have enough questions for your query.',
        2: 'Invalid parameter. Please check your inputs.',
        3: 'Session token not found.',
        4: 'Token empty.',
        5: 'Too many requests.'
        }

        if (data.response_code !== 0) {
            this.error = errorMessages[data.response_code] + ' Please try again.'
            return
        }

        this.questions = data.results
      } catch (err) {
        this.error = 'Something went wrong fetching questions. Please try again.'
        console.error(err)
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=DM+Sans:wght@300;400;500&display=swap');

:global(body) { margin: 0; padding: 0; }
* { box-sizing: border-box; margin: 0; padding: 0; }

/*  Layout  */
.trivia-wrapper {
  font-family: 'DM Sans', sans-serif;
  height: 100dvh;
  background-image: linear-gradient(180deg, #05324E 56.25%, #164D61 70.31%, #276A75 83.33%, #5EC4B4);
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  padding: 32px 24px;
  gap: 24px;
  overflow: hidden;
}

/* left card */
.card {
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid #5EC4B4;
  border-radius: 20px;
  width: 100%;
  max-width: 460px;
  flex-shrink: 0;
  overflow: hidden;
  backdrop-filter: blur(12px);
  align-self: center;
}

.card-header {
  padding: 32px 36px 24px;
  border-bottom: 1px solid rgba(255,255,255,0.07);
  text-align: center;
}

h1 {
  font-family: 'DM Sans', sans-serif;
  font-size: 1.9rem;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: -0.5px;
}

.subtitle {
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.88rem;
  margin-top: 4px;
}

/* Form  */
.form-body {
  padding: 24px 36px 32px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.field-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

label {
  font-size: 0.78rem;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: rgba(255, 255, 255, 0.726);
}

.text-input,
.dropdown {
  background: #e7e7e7;
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 10px;
  color: black;
  font-family: 'DM Sans', sans-serif;
  font-size: 0.95rem;
  padding: 11px 14px;
  width: 100%;
  outline: none;
}

.text-input:focus,
.dropdown:focus {
  background: white;
}

.text-input::placeholder { color: rgba(0, 0, 0, 0.356); }

.dropdown { appearance: none; -webkit-appearance: none; cursor: pointer; padding-right: 36px; }

.select-wrapper { position: relative; }

.chevron {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(0,0,0,0.4);
  pointer-events: none;
  font-size: 1rem;
}

.hint {
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.384);
}

/* Button */
.submit-btn {
  margin-top: 4px;
  padding: 14px;
  border: 2px solid #5EC4B4;
  border-radius: 12px;
  background: #05324E;
  color: #5EC4B4;
  font-family: 'DM Sans', sans-serif;
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.15s;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 50px;
}

.submit-btn:hover:not(:disabled) { opacity: 0.88; transform: translateY(-1px); }
.submit-btn:disabled { opacity: 0.5; cursor: not-allowed; }

.spinner {
  width: 20px; height: 20px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  display: inline-block;
}

.large-spinner {
  width: 36px;
  height: 36px;
  margin-bottom: 12px;
}

@keyframes spin { to { transform: rotate(360deg); } }

/* Error */
.error-msg {
  color: #f87171;
  font-size: 0.85rem;
  background: rgba(248, 113, 113, 0.08);
  border: 1px solid rgba(248, 113, 113, 0.2);
  border-radius: 8px;
  padding: 10px 14px;
}

/* Right Results Panel */
.results-panel {
  flex: 1;
  max-width: 560px;
  max-height: 591px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid #5EC4B4;
  border-radius: 20px;
  backdrop-filter: blur(12px);
  overflow: hidden;
}

.results-header {
  padding: 20px 24px 16px;
  border-bottom: 1px solid rgba(255,255,255,0.07);
  flex-shrink: 0;
}

.results-header h2 {
  font-family: 'DM Sans', sans-serif;
  color: rgba(255,255,255,0.6);
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

/* scrollable list */
.results-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.results-scroll::-webkit-scrollbar { width: 4px; }
.results-scroll::-webkit-scrollbar-track { background: transparent; }
.results-scroll::-webkit-scrollbar-thumb {
  background: rgba(94, 196, 180, 0.3);
  border-radius: 4px;
}

/* Empty / Loading state */
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: rgba(255,255,255,0.25);
  font-size: 0.9rem;
}

.empty-icon {
  font-size: 2.5rem;
  opacity: 0.4;
}

/* Question Cards */
.question-card {
  display: flex;
  gap: 14px;
  background: rgba(255,255,255,0.035);
  border: 1px solid rgba(255,255,255,0.07);
  border-radius: 14px;
  padding: 16px;
  align-items: flex-start;
  flex-shrink: 0;
}

.q-number {
  font-family: 'DM Sans', sans-serif;
  font-weight: 800;
  font-size: 0.8rem;
  color: #5EC4B4;
  min-width: 24px;
  padding-top: 2px;
}

.q-text {
  color: #e8f8f6;
  font-size: 0.92rem;
  line-height: 1.5;
  margin-bottom: 10px;
}

.tags { display: flex; flex-wrap: wrap; gap: 6px; }

.tag {
  font-size: 0.7rem;
  font-weight: 500;
  padding: 3px 9px;
  border-radius: 20px;
  text-transform: capitalize;
}

.category-tag { background: rgba(94, 196, 180, 0.12); color: #5EC4B4; }
.type-tag     { background: rgba(255,255,255,0.07);   color: rgba(255,255,255,0.4); }
.difficulty-easy    { background: rgba(52, 211, 153, 0.12); color: #6ee7b7; }
.difficulty-medium  { background: rgba(251, 191, 36, 0.12); color: #fcd34d; }
.difficulty-hard    { background: rgba(248, 113, 113, 0.12); color: #fca5a5; }

/* Responsive */
@media (max-width: 768px) {
  .trivia-wrapper {
    flex-direction: column;
    height: auto;
    overflow: auto;
    padding: 24px 16px;
  }

  .card { max-width: 100%; }

  .results-panel {
    width: 100%;
    max-width: 100%;
    height: 480px;
  }
}
</style>