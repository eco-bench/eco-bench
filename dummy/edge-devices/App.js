const fs = require('fs');
const axios = require('axios');
const FormData = require('form-data');


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


function camera() {
    setTimeout(async () => {
        try {
            console.log('Start sending video snippet')
            const form = new FormData();
            form.append('file', fs.createReadStream('./assets/file_example_MP4_640_3MG.mp4'));
            await axios({
                method: 'POST',
                url: `${process.env.WORKER_IP}:${process.env.WORKER_PORT}/${process.env.WORKER_ENDPOINT}`,
                data: form,
                headers: form.getHeaders()
            })
            console.log('Done sending video snippet')
            camera()
        } catch (e) {
            console.error('Error sending camera image: ', e)
        }
    }, process.env.PING_INTERVAL)
}

function sensor() {
    setTimeout(async () => {
        try {
            await axios({
                method: 'POST',
                url: `${process.env.WORKER_IP}:${process.env.WORKER_PORT}/${process.env.WORKER_ENDPOINT}`,
                data: {
                    measurement: Math.random() * 100
                },
            });
            sensor()
        } catch(e) {
            console.error('Error sending sensor data: ', e)
        }
    }, process.env.PING_INTERVAL)

}