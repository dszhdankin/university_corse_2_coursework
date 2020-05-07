from io import BytesIO
from math import ceil
from zipfile import ZipFile
from PIL import Image
import random
import psycopg2
import sys
import keras
import numpy


db = psycopg2.connect(dbname='neural_network_visual_creator_db',
                      host='localhost',
                      user='postgres',
                      password='#Jagarvasya1')
cursor = db.cursor()
cursor.execute('SELECT dataset FROM users WHERE username=' + '\'' + sys.argv[1] + '\'')
view = cursor.fetchone()
bin_data = view[0].tobytes()
bytes_input = BytesIO(bin_data)
zip_archive = ZipFile(bytes_input, 'r')


def form_train_data(archive):
    manifest_items = archive.read('manifest.csv').decode('UTF-8').split('\n')
    for i in range(len(manifest_items)):
        manifest_items[i] = manifest_items[i].split(',')

    if manifest_items[1][2] == 'jpg':
        heights = []
        widths = []
        for i in range(1, len(manifest_items)):
            if len(manifest_items[i]) != 3:
                break
            image = Image.open(BytesIO(archive.read(manifest_items[i][0])))
            image_data = numpy.array(image)
            heights.append(image_data.shape[0])
            widths.append(image_data.shape[1])
        avr_height = ceil(sum(heights) / len(heights))
        avr_width = ceil(sum(widths) / len(widths))
        picture_side = int((avr_height + avr_width) / 2)
        train_data = []
        for i in range(1, len(manifest_items)):
            if len(manifest_items[i]) != 3:
                break
            image = Image.open(BytesIO(archive.read(manifest_items[i][0])))
            image = image.convert('RGB')
            image = image.resize((picture_side, picture_side), Image.ANTIALIAS)
            train_data.append((numpy.array(image), int(manifest_items[i][1])))

        random.shuffle(train_data)
        train_images = []
        train_labels = []
        for cur in train_data:
            train_images.append(cur[0])
            train_labels.append(cur[1])

        return train_images, train_labels, picture_side

    else:
        pass
