const fs = require('fs');
const axios = require('axios');
const FormData = require('form-data');
const dotenv = require('dotenv');

// load config
dotenv.config();


switch (process.env.DEVICE_TYPE) {
    case 'camera':
        camera();
        break;
    case 'sensor':
        sensor();
        break;
    default:
        console.log('You must define a device type (\'camera\', \'sensor\') through \'process.env.DEVICE_TYPE\'')
}


async function camera() {
    let stopped = false
    while (!stopped) {
        try {
            console.log('Start sending video snippet')
            const form = new FormData();
            form.append('file', fs.createReadStream('./assets/file_example_MP4_640_3MG.mp4'));
            await axios({
                method: 'POST',
                url: process.env.API_URL,
                data: form,
                headers: form.getHeaders()
            })
            console.log('Done sending video snippet')
        } catch (e) {
            console.error('Stopping camera: ', e)
            stopped = true
        }
    }

}

async function sensor() {


}