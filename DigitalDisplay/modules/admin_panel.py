__author__ = 'lasantha'

import os
import shutil
import signal
import start_up
import sequence_runner
from subprocess import check_output
import gtk.gdk
from lxml import etree
import logging
import kernel_utils
import subprocess
import urllib
import base64
import math
import json
import random
import string

LOGGER = logging.getLogger('wso2server.resource_types')


class DisplayAdmin:

    cont_conf_path = "../resources/conf/digital_display_content.xml"
    # resource_paths = []
    resource_names = []
    sequence = {}
    packet_size = 3000

    def __init__(self):
        self.set_resource_atrributes()
        LOGGER.info("Admin Panel Created")

    # shutdown process for given process name
    def shutdown_process(self, name):
        pid = self.get_pid(name).replace('\n', '').split(" ")
        for x in pid:
            os.kill(int(x), signal.SIGKILL)

    # kill python process
    def terminate_display(self):
        try:
            return "Success||Digital Display Terminated Successfully"
        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return "Failed||Digital Display Failed To Terminate "
        finally:
            self.shutdown_process("python")

    def terminate_server(self):
        start_up.ContentUtilityClass().terminate_server()

    def start_server(self):
        start_up.ContentUtilityClass().start_server(kernel_utils.web_content_path)

    def restart_server(self):
        try:
            self.terminate_server()
            self.start_server()
            return "Success||Server Restarted Successfully "
        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return "Failed||Digital Display Failed To Restart Server "

    def restart_display(self):
        try:
            return "Success||Digital Display Restarted Successfully"
        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return "Failed||Digital Display Failed To Restart"
        finally:
            subprocess.Popen("sudo reboot", shell=True)

    def start_browser(self):
        start_up.ContentUtilityClass().open_browser()

    def close_browser(self):
        start_up.ContentUtilityClass().close_browser()

    def restart_browser(self):
        try:
            self.close_browser()
            self.start_browser()
            return "Success||Digital Display Browser Restarted Successfully"
        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return "Failed||Digital Display Failed To Restart Browser"

    def get_pid(self, name):
        return check_output(["pidof", name])

    # upload file(html, image, js, css) to /resources/www folder
    def upload_content(self, remote_path, screen_name):
        try:
            download_file = urllib.URLopener()
            download_file.retrieve(remote_path, '../resources/www/'+self.sequence[screen_name]+"/"+remote_path.split('/')[-1])
            return "Success||Upload The File " + remote_path.split('/')[-1]+" To "+screen_name + " Successfully"
        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return "Failed||Digital Display Failed To Upload File"

    # read content config xml file
    def read_conf(self):
        # XMLParser parameters----->>> ns_clean=True,remove_comments=True,remove_blank_text=True
        parser = etree.XMLParser()
        tree = etree.parse(self.cont_conf_path, parser)
        return tree

    # change running sequence list
    def change_sequence_list(self):
        start_up.ContentUtilityClass().set_resources_conf()
        seq_runner = sequence_runner.SequenceRunner()
        seq_runner.set_current_resources_conf(start_up.ContentUtilityClass().get_resources_conf())
        seq_runner.set_sequence()

    # edit content config xml file
    def edit_sequence(self, page_no, attr, new_val):
        try:
            tree = self.read_conf()
            root = tree.getroot()
            nsp = root.nsmap
            # read content xml file and append displaying path set to path_array
            self.set_resource_atrributes()
            # iterate through xml file
            for elem in root.iter():
                # if elem.tag == '{'+nsp[None]+'}'+'Resource' and elem.attrib['path'] == self.resource_paths[int(page_no)-1]:
                if elem.tag == '{'+nsp[None]+'}'+'Resource' and elem.attrib['path'] == self.sequence[page_no]:
                    elem.set(attr, new_val)
                    break

            tree.write(self.cont_conf_path)
            self.change_sequence_list()
            self.set_resource_atrributes()
            return "Success||Sequence Changed Successfully"
        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return "Failed||Digital Display Failed To Change Sequence"

    # create child resource
    def build_resource(self, type_, time_, path_, name_):
        child = etree.Element('Resource')
        child.set('type', type_)
        child.set('time', time_)
        child.set('path', path_)
        child.set('name', name_)
        return child

    # read content conf xml and update path_array
    def set_resource_atrributes(self):
        tree = self.read_conf()
        root = tree.getroot()
        nsp = root.nsmap
        paths_n_names = {}
        # paths = []
        names = []

        for elem in root.iter():
            if elem.tag == '{'+nsp[None]+'}'+'Resource':
                paths_n_names[elem.attrib['name']] = elem.attrib['path']
                # paths.append(elem.attrib['path'])
                names.append(elem.attrib['name'])

        self.sequence = paths_n_names
        # self.resource_paths = paths
        self.resource_names = names

    # add new resource to content conf xml file
    def add_new_resource(self, *args):
        try:
            # if type is folder type create folder in /resource/www path and name it as path name
            if args[0] == "folder":
                newpath = '../resources/www/'+args[2]
                if not os.path.exists(newpath):
                    os.makedirs(newpath)

            tree = self.read_conf()
            root = tree.getroot()
            nsp = root.nsmap

            # default
            if len(args) == 4:
                for elem in root.iter():
                    if elem.tag == '{'+nsp[None]+'}'+'DisplaySequence':
                        child = self.build_resource(args[0], args[1], args[2], args[3])
                        elem.append(child)
                        break
            # if user want to add resource into specific place
            elif len(args) == 5:
                self.set_resource_atrributes()
                if "before=" in args[4]:
                    for elem in root.iter():
                        # if elem.tag == '{'+nsp[None]+'}'+'Resource' and elem.attrib['path'] == self.resource_paths[int(args[3].split('=')[1])-1]:
                        if elem.tag == '{'+nsp[None]+'}'+'Resource' and elem.attrib['path'] == self.sequence[args[4].split('=')[1]]:
                            child = self.build_resource(args[0], args[1], args[2], args[3])
                            elem.addprevious(child)
                            break
                elif "after=" in args[4]:
                    for elem in root.iter():
                        # if elem.tag == '{'+nsp[None]+'}'+'Resource' and elem.attrib['path'] == self.resource_paths[int(args[3].split('=')[1])-1]:
                        if elem.tag == '{'+nsp[None]+'}'+'Resource' and elem.attrib['path'] == self.sequence[args[4].split('=')[1]]:
                            child = self.build_resource(args[0], args[1], args[2], args[3])
                            elem.addnext(child)
                            break
            else:
                return "Failed||Digital Display Failed To Add New Resource"

            tree.write(self.cont_conf_path)
            self.change_sequence_list()
            self.set_resource_atrributes()
            return "Success||Added New Resource To The Sequence Successfully"
        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return "Failed||Digital Display Failed To Add New Resource"

    # remove resource from cont conf xml file
    def remove_resources(self, screen_name):
        try:
            tree = self.read_conf()
            root = tree.getroot()
            nsp = root.nsmap
            self.set_resource_atrributes()
            for elem in root.iter():
                if elem.tag == '{'+nsp[None]+'}'+'DisplaySequence':
                    for re in elem.iter():
                        # if re.tag == '{'+nsp[None]+'}'+'Resource' and re.attrib['path'] == self.resource_paths[int(page_no)-1]:
                        if re.tag == '{'+nsp[None]+'}'+'Resource' and re.attrib['path'] == self.sequence[screen_name]:
                            if re.attrib['type'] == 'folder':
                                self.remove_dir_and_content(self.sequence[screen_name])
                            re.getparent().remove(re)
                            break

            tree.write(self.cont_conf_path)
            self.change_sequence_list()
            self.set_resource_atrributes()
            return "Success||Remove Resource From The Sequence Successfully"
        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return "Failed||Digital Display Failed To Remove Resource"

    # remove folder in /resources/www path include files in folder
    def remove_dir_and_content(self, dir_name):
        try:
            shutil.rmtree('../resources/www/'+dir_name)
            return "Success||Directory And Content Removed Successfully"
        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return "Failed||Digital Display Failed To Remove Directory And Content"

    def get_running_screen(self):
        try:
            w = gtk.gdk.get_default_root_window()
            sz = w.get_size()
            pb = gtk.gdk.Pixbuf(gtk.gdk.COLORSPACE_RGB, False, 8, sz[0], sz[1])
            pb = pb.get_from_drawable(w, w.get_colormap(), 0, 0, 0, 0, sz[0], sz[1])

            if pb is not None:
                pb.save("../resources/www/screenshot/screen.png", "png")
                return True
            else:
                return False

        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return False

    def get_encode_string(self):
        with open("../resources/www/screenshot/screen.png", "rb") as image_file:
            encoded_string = base64.b64encode(image_file.read())
            return encoded_string

    def get_random_word(self, length):
        return ''.join(random.choice(string.lowercase) for i in range(length))

    def get_screenshot(self):
        try:
            if self.get_running_screen():
                encoded = self.get_encode_string()
                end = self.packet_size
                start = 0
                length = len(encoded)
                pic_id = self.get_random_word(8)
                pos = 0
                no_of_packets = math.ceil(length/self.packet_size)
                data_chunks = []

                while start <= len(encoded):
                    data = {"data": encoded[start:end], "pic_id": pic_id, "pos": pos, "size": no_of_packets}
                    print pos+1
                    data_chunks.insert(pos, json.JSONEncoder().encode(data))
                    end += self.packet_size
                    start += self.packet_size
                    pos += 1
                return data_chunks
            else:
                return "Failed||Digital Display Failed To Capture Screenshot"
        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return "Failed||Digital Display Failed To Capture Screenshot"

    def get_content_list(self):
        try:
            self.set_resource_atrributes()
            return "ContentList||"+str("-".join(self.resource_names))
        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return "Failed||Digital Display Failed To Get Content List"

    def get_device_status(self):
        try:
            device_status = ""

            cpu_temp = self.get_cpu_temperature()
            device_status += "CPU Temperature : "+str(cpu_temp)+"-"

            cpu_usage = self.get_cpu_usage()
            device_status += "CPU Usage : "+str(cpu_usage)+"%-"

            ram_info = self.get_ram_info()
            total_ram = round(int(ram_info[0]) / 1000, 1)
            device_status += "Total Ram : "+str(total_ram)+" KB-"

            ram_used = round(int(ram_info[1]) / 1000, 1)
            device_status += "Ram Used : "+str(ram_used)+" KB-"

            ram_free = round(int(ram_info[2]) / 1000, 1)
            device_status += "Ram Free : "+str(ram_free)+" KB-"

            disk_space = self.get_disk_space()
            device_status += "Total Disk : "+str(disk_space[0])+"-"
            device_status += "Free Disk : "+str(disk_space[1])+"-"
            device_status += "Disk percentage : "+str(disk_space[3])
            return "DeviceStatus||"+device_status
        except Exception as e:
            LOGGER.warning("Exception"+str(e))
            return "Failed||Digital Display Failed To Load Device Status"

    # Return CPU temperature as a character string
    def get_cpu_temperature(self):
        res = os.popen('vcgencmd measure_temp').readline()
        return res.replace("temp=", "").replace("'C\n", "")

    # Return RAM information (unit=kb) in a list
    # Index 0: total RAM
    # Index 1: used RAM
    # Index 2: free RAM
    def get_ram_info(self):
        p = os.popen('free')
        i = 0
        while 1:
            i += 1
            line = p.readline()
            if i == 2:
                return line.split()[1:4]

    # Return % of CPU used by user as a character string
    def get_cpu_usage(self):
        return str(os.popen("top -n1 | awk '/Cpu\(s\):/ {print $2}'").readline().strip('\n'))

    # Return information about disk space as a list (unit included)
    # Index 0: total disk space
    # Index 1: used disk space
    # Index 2: remaining disk space
    # Index 3: percentage of disk used
    def get_disk_space(self):
        p = os.popen("df -h /")
        i = 0
        while 1:
            i += 1
            line = p.readline()
            if i == 2:
                return line.split()[1:5]

