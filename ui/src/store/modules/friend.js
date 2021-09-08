
const friend = {
    state: {
        timeDate: ''
    },

    mutations: {
        SET_TIMR: (state, time) => {
            state.timeDate = time
        },
    },

    actions: {
        SetTime({ commit }, time) {
            return new Promise(() => {
                commit('SET_TIMR',time)
            })
        },
    }
}

export default friend
