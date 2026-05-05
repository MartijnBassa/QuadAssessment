import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import TriviaForm from '../components/triviaForm.vue'

const mockFetch = vi.fn()
vi.stubGlobal('fetch', mockFetch)

const SAMPLE_QUESTIONS = [
  {
    question: 'Route 66 in the United States spans the entire mainland of America, from California to New York.',
    options: ['True', 'False'],
    category: 'Geography',
    difficulty: 'medium',
    type: 'boolean',
  },
  {
    question: 'When did the website "Facebook" launch?',
    options: ['2006', '2005', '2003', '2004'],
    category: 'General Knowledge',
    difficulty: 'medium',
    type: 'multiple',
  },
]

function mockQuizFetch(questions = SAMPLE_QUESTIONS) {
  mockFetch.mockResolvedValue({
    ok: true,
    json: async () => ({ quizId: 'test-quiz-id', results: questions }),
  })
}

describe('TriviaForm', () => {
  beforeEach(() => {
    mockFetch.mockReset()
  })

  // --- Initial state ---

  it('shows empty state message before any quiz is generated', () => {
    const wrapper = mount(TriviaForm)
    expect(wrapper.text()).toContain('Your questions will appear here')
  })

  it('does not show the footer before questions are loaded', () => {
    const wrapper = mount(TriviaForm)
    expect(wrapper.find('.quiz-footer').exists()).toBe(false)
  })

  // --- Fetching questions ---

  it('renders questions after a successful fetch', async () => {
    mockQuizFetch()
    const wrapper = mount(TriviaForm)
    await wrapper.find('.submit-btn').trigger('click')
    await flushPromises()
    expect(wrapper.findAll('.question-card')).toHaveLength(2)
  })

  // --- Answer selection ---

  it('selectAnswer records the chosen option', () => {
    const wrapper = mount(TriviaForm)
    wrapper.vm.selectAnswer(0, 'False')
    expect(wrapper.vm.selectedAnswers[0]).toBe('False')
  })

  it('selectAnswer overwrites a previous pick for the same question', () => {
    const wrapper = mount(TriviaForm)
    wrapper.vm.selectAnswer(0, 'True')
    wrapper.vm.selectAnswer(0, 'False')
    expect(wrapper.vm.selectedAnswers[0]).toBe('False')
  })

  // --- Computed: answeredCount / allAnswered ---

  it('answeredCount starts at zero', () => {
    const wrapper = mount(TriviaForm)
    expect(wrapper.vm.answeredCount).toBe(0)
  })

  it('allAnswered is true when every question has been answered', async () => {
    const wrapper = mount(TriviaForm)
    wrapper.vm.questions = SAMPLE_QUESTIONS
    await wrapper.vm.$nextTick()
    wrapper.vm.selectAnswer(0, 'False')
    wrapper.vm.selectAnswer(1, '2004')
    expect(wrapper.vm.allAnswered).toBe(true)
  })

  it('Submit Answers button is disabled until all questions are answered', async () => {
    mockQuizFetch()
    const wrapper = mount(TriviaForm)
    await wrapper.find('.submit-btn').trigger('click')
    await flushPromises()
    const submitBtn = wrapper.find('.submit-quiz-btn')
    expect(submitBtn.attributes('disabled')).toBeDefined()
    wrapper.vm.selectAnswer(0, 'False')
    wrapper.vm.selectAnswer(1, '2004')
    await wrapper.vm.$nextTick()
    expect(submitBtn.attributes('disabled')).toBeUndefined()
  })

  // --- getOptionClass ---

  it('returns "correct" for the right answer after submit', async () => {
    const wrapper = mount(TriviaForm)
    wrapper.vm.submitted = true
    wrapper.vm.answerResults = { 0: { correct: false, correctAnswer: 'False' } }
    await wrapper.vm.$nextTick()
    expect(wrapper.vm.getOptionClass(0, 'False')).toBe('correct')
  })

  it('returns "wrong" for the incorrectly chosen option after submit', async () => {
    const wrapper = mount(TriviaForm)
    wrapper.vm.selectAnswer(0, 'True')
    wrapper.vm.submitted = true
    wrapper.vm.answerResults = { 0: { correct: false, correctAnswer: 'False' } }
    await wrapper.vm.$nextTick()
    expect(wrapper.vm.getOptionClass(0, 'True')).toBe('wrong')
  })
})
