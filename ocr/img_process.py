import cv2
import os
from PIL import Image

if 'ocr' in os.getcwd():
  CWD_PATH = os.path.abspath(os.path.join(os.getcwd(), "..", "..", 'ocr-android'))
else :
  CWD_PATH = os.path.abspath(os.path.join(os.getcwd(), "..", 'ocr-android'))
  
DATA_PATH = os.path.join(CWD_PATH, 'data')
RESIZE_IMG = (360,640)


def get_list_image():
  img_array = os.listdir(DATA_PATH)
  for i in range(len(img_array)):
    img_array[i] = os.path.join(DATA_PATH, img_array[i])
  return img_array

def get_filename_from_path(path):
  filename = path.split('\\')[-1].split('.')[0]
  return filename

def read_image(path : str):
  img = cv2.imread(path)
  if (img is None):
    print("None Image Detected")
  return img

def display_image(path, img, resize_scale=1):
  filename = get_filename_from_path(path)
  h, w = img.shape[:2]
  h = int(h * resize_scale)
  w = int(w * resize_scale)
  img = cv2.resize(img, (w, h))
  cv2.imshow(filename, img)
  cv2.waitKey(0)
  cv2.destroyAllWindows()
