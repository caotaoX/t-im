

const drawer = {
    state: {
        contacti: {},
        edit: false
    },

    mutations: {
        SET_CONTACT: (state, contacti) => {
            state.contacti = contacti
        },
        SET_EDIT: (state, edit) => {
            state.edit = edit
        },
    },

    actions: {
        SetContact({ commit }, contacti) {
            return new Promise(() => {
                commit('SET_CONTACT', contacti)
            })
        },

        SetEdit({ commit }, edit) {
            return new Promise(() => {
                commit('SET_EDIT', edit)
            })
        },
    }
}


export default drawer
