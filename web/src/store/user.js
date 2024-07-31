import $ from 'jquery';

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
        pulling_info: true,
    },
    getters: {
    },
    // 一般用来修改数据
    mutations: {
        // 把user赋值给state
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        // 辅助函数获取token
        updateToken(state, token) {
            state.token = token;
        },
        // 清空token
        logout(state) {
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
        updatePullingInfo(state, pulling_info) {
            state.pulling_info = pulling_info;
        }

    },
    actions: {
        // 把前端输入post到后端，并从后端获取返回值
        login(context, data) {
            $.ajax({
                url: "https://app7033.acapp.acwing.com.cn/api/user/account/token/",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        localStorage.setItem("jwt_token", resp.token);
                        context.commit("updateToken", resp.token);
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                },
            });
        },
        //      获取信息
        getinfo(context, data) {
            $.ajax({
                url: "https://app7033.acapp.acwing.com.cn/api/user/account/info/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + context.state.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        context.commit("updateUser", {
                            ...resp, // 解析resp
                            is_login: true,
                        });
                        // 调用回调函数
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }

                },
                error(resp) {
                    data.error(resp);
                }
            });
        },
        // 调用logout
        logout(context) {
            localStorage.removeItem("jwt_token");
            context.commit("logout");
        }


    },
    modules: {

    }
}