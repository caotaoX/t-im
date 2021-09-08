
const getters = {
  token: state => state.user.token,
  user: state=> state.user,
  timeDate: state=> state.friend.timeDate,
  voice: state=> state.setting.voice,
  browserJitter: state=> state.setting.browserJitter,
  contacti : state=> state.drawer.contacti
}
export default getters
