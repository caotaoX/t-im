<script>
export default {
  created() {
    let u = navigator.userAgent;
    let mobile = !!u.match(/AppleWebKit.*Mobile.*/);
    if(mobile) {
      this.$router.push('/mobile');
    } else {
      this.$router.push('/computer');
    }
  },
  render: function(h) {
    return h()
  }
}
</script>
