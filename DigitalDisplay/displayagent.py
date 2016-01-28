from modules.start_up import *
import modules.mqtt_client


# ---------------------------------------------------------------------------
#   Main method
# ---------------------------------------------------------------------------
def main():

    digital_display = ContentUtilityClass()

    digital_display.create_logger()

    # global sequence_runner
    LOGGER.info("Server Starting...")

    # read configurations
    digital_display.read_kernel_config()
    digital_display.read_content_config()

    # starting server
    digital_display.start_server(kernel_utils.web_content_path)

    # start polling repositories
    # poll_update(safe_exit_handler)

    # start sequence
    sequence_runner = digital_display.get_sequence_runner()
    # time.sleep(60000)  # startup delay for updating

    # sequence_runner.update_content()

    mqtt = threading.Thread(target=modules.mqtt_client.main, args=())
    mqtt.start()

    agent = threading.Thread(target=sequence_runner.run_sequence, args=())
    agent.start()

if __name__ == '__main__':
    main()
