import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
  {
    path: '',
    redirect: '/login',
  },
  {
    path: '/login',
    name: "Login",
    component: () => import( "@/views/Login"),
  },
  {
    path: '/home',
    name: "Home",
    component: () => import( "@/views/Home"),
  },
  {
    path: '/lp',
    name: "Lm",
    component: () => import( "@/views/Lp"),
  },
];

const router = new VueRouter({
  mode: 'history', // 去掉url中的#
  scrollBehavior: () => ({ y: 0 }),
  routes
});

export default router;
