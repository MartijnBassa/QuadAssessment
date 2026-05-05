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
            v-for="(question, questionIndex) in questions"
            :key="questionIndex"
            class="question-card"
          >
            <span class="q-number">{{ questionIndex + 1 }}</span>
            <div class="q-content">
              <p class="q-text">{{ question.question }}</p>

              <div class="q-options">
                <button
                  v-for="(option, optionIndex) in question.options"
                  :key="optionIndex"
                  class="option-btn"
                  :class="getOptionClass(questionIndex, option)"
                  :disabled="submitted"
                  @click="selectAnswer(questionIndex, option)"
                >{{ option }}</button>
              </div>
               <div class="tags">
                <span class="tag category-tag">{{ question.category }}</span>
                <span class="tag" :class="`difficulty-${question.difficulty}`">{{ question.difficulty }}</span>
                <span class="tag type-tag">{{ question.type === 'boolean' ? 'True/False' : 'Multiple Choice' }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Footer submit or score -->
        <div class="quiz-footer">
          <template v-if="!submitted">
            <span class="answered-count">{{ answeredCount }} / {{ questions.length }} answered</span>
            <button
              class="submit-quiz-btn"
              :disabled="!allAnswered"
              @click="submitQuiz"
            >Submit Answers</button>
          </template>
          <template v-else>
            <span class="score-display">{{ quizScore }} / {{ questions.length }} correct</span>
            <button class="retry-btn" @click="fetchTrivia">New Quiz</button>
          </template>
        </div>

      </template>

    </div>

  </div>
</template>

<script>
import triviaOptions from '../trivia-options.json'

const API_BASE = 'http://localhost:8080/api'

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
      quizId: null,
      selectedAnswers: {},
      answerResults: {},
      quizScore: null,
      submitted: false,
      isLoading: false,
      error: null
    }
  },

  computed: {
    answeredCount() {
      return Object.keys(this.selectedAnswers).length
    },
    allAnswered() {
      return this.questions.length > 0 && this.answeredCount === this.questions.length
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
      this.quizId = null
      this.selectedAnswers = {}
      this.answerResults = {}
      this.quizScore = null
      this.submitted = false

      try {
        const params = new URLSearchParams({ amount: this.form.amount })

        if (this.form.category !== 'any') params.append('category', this.form.category)
        if (this.form.difficulty !== 'any') params.append('difficulty', this.form.difficulty)
        if (this.form.type !== 'any') params.append('type', this.form.type)

        const url = `${API_BASE}/questions?${params.toString()}`
        const response = await fetch(url)
        const data = await response.json()

        if (!response.ok) {
          this.error = data.error ?? 'Something went wrong. Please try again.'
          return
        }

        this.quizId = data.quizId
        this.questions = data.results

      } catch (err) {
        this.error = 'Network error. Please try again.'
      } finally {
        this.isLoading = false
      }
    },

    selectAnswer(questionIndex, answer) {
      this.selectedAnswers[questionIndex] = answer
    },

    async submitQuiz() {
      try {
        const response = await fetch(`${API_BASE}/checkanswers`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ quizId: this.quizId, answers: this.selectedAnswers })
        })
        const data = await response.json()

        if (!response.ok) {
          this.error = data.error ?? 'Failed to submit answers.'
          return
        }

        // Convert the results array into an object keyed by question index
        // so we can quickly look up the result for any question: answerResults[0], answerResults[1], etc.
        const resultsByIndex = {}
        for (const result of data.results) {
          resultsByIndex[result.questionIndex] = result
        }
        this.answerResults = resultsByIndex
        this.quizScore = data.score
        this.submitted = true
      } catch (err) {
        this.error = 'Network error. Please try again.'
      }
    },

    getOptionClass(questionIndex, option) {
      // Before the user submits, just highlight their selected option
      if (!this.submitted) {
        const isSelected = this.selectedAnswers[questionIndex] === option
        return isSelected ? 'selected' : ''
      }

      // After submitting, colour each option based on whether it was right or wrong
      const result = this.answerResults[questionIndex]
      if (!result) {
        return ''
      }

      const isCorrectAnswer = option === result.correctAnswer
      const isWrongAnswerPicked = this.selectedAnswers[questionIndex] === option && !result.correct

      if (isCorrectAnswer) {
        return 'correct'
      }
      if (isWrongAnswerPicked) {
        return 'wrong'
      }
      return ''
    }
  }
}
</script>

<style scoped>
@import '../css/triviaForm.css';
</style>
