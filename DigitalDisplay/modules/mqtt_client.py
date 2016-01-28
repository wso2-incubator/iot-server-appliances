__author__ = 'lasantha'
import paho.mqtt.client as mqtt
import admin_panel
import logging
import threading

LOGGER = logging.getLogger('wso2server.httpserver')

obj = admin_panel.DisplayAdmin()
server = None
port = None
keep_alive = 60
user_id = None
user_name = None


# The callback for when the client receives a CONNACK response from the server.
def on_connect(client, userdata, flags, rc):
    LOGGER.info("MQTT Connected with result code "+str(rc))
    client.subscribe("wso2/iot/"+get_username()+"/digital_display/"+get_id()+"/digital_display_subscriber", 2)
    print "wso2/iot/"+get_username()+"/digital_display/"+get_id()+"/digital_display_subscriber"
    # Subscribing in on_connect() means that if we lose the connection and
    # reconnect then subscriptions will be renewed.


# The callback for when PUBLISH message to server.
def on_publish(client, userdata, mid):
    LOGGER.info("MQTT on_publish callback ")


# The callback for when SUBSCRIBE to a topic
def on_subscribe(client, userdata, mid, granted_qos):
    LOGGER.info("MQTT on_subscribe callback ")


# The callback for when a PUBLISH message is received from the server.
def on_message(client, userdata, msg):
    LOGGER.info("Message Received From Server")
    LOGGER.info("Message :- "+msg.topic+" "+str(msg.payload))

    executing_method = threading.Thread(target=execute_method, args=(msg.payload, client,))
    executing_method.start()


# When a PUBLISH message is received from the server on_message callback call to execute method in Thread
def execute_method(payload, client):

    global result
    result = None

    method_name = payload.split('::')
    received_id = method_name[0]
    args = method_name[2].split('|')
    if len(args) is 1 and args[0] is '':
        result = getattr(obj, method_name[1])()
    else:
        result = getattr(obj, method_name[1])(*args)

    if result is not None and type(result) is str:
        client.publish("wso2/iot/"+get_username()+"/digital_display/"+get_id()+"/digital_display_publisher", received_id+"::"+str(result), qos=2, retain=False)
    elif type(result) is list:
        for x in result:
            client.publish("wso2/iot/"+get_username()+"/digital_display/"+get_id()+"/digital_display_publisher", received_id+"::"+"token"+"::"+x, qos=2, retain=False)


def read_server_conf():
    with open("../deviceConfig.properties", 'r') as outfile:
        conf_file = outfile.readlines()

    return conf_file


def get_id():
    if user_id is None:
        set_id()
        return user_id
    else:
        return user_id


def set_id():
    global user_id
    conf_file = read_server_conf()
    user_id = [sub for sub in conf_file if 'deviceId=' in sub]
    user_id = user_id[0].split('=')[1].replace("\n", "")


def get_username():
    if user_name is None:
        set_username()
        return user_name
    else:
        return user_name


def set_username():
    global user_name
    conf_file = read_server_conf()
    user_name = [sub for sub in conf_file if 'owner=' in sub]
    user_name = user_name[0].split('=')[1].replace("\n", "")


def get_connection_settings():
    if server is None or port is None:
        set_connection_settings()
        return server
    else:
        return server


def set_connection_settings():
    global server
    global port
    conf_file = read_server_conf()
    settings = [sub for sub in conf_file if 'mqtt-ep=' in sub]
    settings = settings[0].split('=')[1].split(':')
    server = settings[1].replace("//", "")
    port = int(settings[2].replace("\n", ""))


# main method
def main():
    set_connection_settings()
    client = mqtt.Client()
    LOGGER.info("Client successfully created ")
    client.on_connect = on_connect
    client.on_message = on_message
    client.on_publish = on_publish
    client.on_subscribe = on_subscribe

    while True:
        try:
            client.connect(server, port)
            client.loop_forever()
        except (KeyboardInterrupt, Exception) as e:
            LOGGER.info("MQTT_LISTENER: Exception in MQTTServerThread [either KeyboardInterrupt or Other]")
            LOGGER.info("MQTT_LISTENER: " + str(e))
            client.disconnect()
            continue
