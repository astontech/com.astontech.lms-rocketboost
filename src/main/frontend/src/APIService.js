import axios from 'axios'
const API_URL = 'http://localhost:8080';

export default class APIService {
    constructor(){}

    //region Paths

        //region GET
            getPaths() {
                const url = `${API_URL}/api/paths`;
                return axios.get(url).then(response => response.data);
            }

            getPath(id) {
                const url = `${API_URL}/api/paths/${id}`;
                return axios.get(url).then(response => response.data);
            }
        //endregion

        //region POST
            newPath(path) {
                const url = `${API_URL}/api/paths`;
                return axios.post(url, path).then(response => response.data);
            }
        //endregion

        //region PATCH
            updatePath(path) {
                const url = `${API_URL}/api/paths`;
                return axios.post(url, path).then(response => response.data);
            }
        //endregion

        //region DELETE
        // TODO: Finish the @DeleteMapping in PathController first

        //     deletePath(path) {
        //         const url = `${API_URL}/api/paths`;
        //         return axios.post(url, path).then(response => response.data);
        //     }
        //endregion

    //endregion
}