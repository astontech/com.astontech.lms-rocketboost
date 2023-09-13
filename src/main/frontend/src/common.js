export default {
    push(route) {
        this.$router.push(route);
    },
    routeTo(route, name) {
        let goto = false;

        for (let n of this.$router.options.routes) {
            if (n.path === route || n.name === name) {
                goto = true;
                break;
            }
        }

        if (goto) {
            this.$router.push(route);
        }
        else {
            this.log("The route '" + route + "' could not be found under '" + name + "' in router.ts!");
            this.snackbar( 'error','An error occurred! The specified page does not exist.', 2.5)
        }
    },
    log(message) {
        // Only allow console in development mode
        if (process.env.NODE_ENV === "development") {
            console.log(message);
        }
    },
    snackbar(type='normal', msg, duration) {
        let response = {
            alertType: type,
            alertMessage: msg,
            btnColor: '#ffffff',
            duration: duration
        };

        switch (type) {
            case 'success':
                response.color = '#4CAF50';
                break;
            case 'info':
                response.color = '#2196F3';
                break;
            case 'warning':
                response.color = '#FB8C00';
                break;
            case 'error':
                response.color = '#FF5252';
                break;
            default:
                response.color = '#323232';
                response.btnColor = 'primary';
        }

        this.$emit('openAlert', response);
    },
    hyphenCase(val) {
        return val.replace(/\W+/g, '-').toLowerCase();
    },
}