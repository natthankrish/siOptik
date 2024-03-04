import cv2 
import os


if 'ocr' in os.getcwd():
  CWD_PATH = os.path.abspath(os.path.join(os.getcwd(), "..", "..", 'ocr-android'))
else :
  CWD_PATH = os.path.abspath(os.path.join(os.getcwd(), "..", 'ocr-android'))
  
DATA_PATH = os.path.join(CWD_PATH, 'data')
RESIZE_IMG = (360,640)


def get_list_image():
  img_array = os.listdir(DATA_PATH)
  res_array = []
  for i in range(len(img_array)):
    if ".jpg" in img_array[i]:
      new_path = os.path.join(DATA_PATH, img_array[i])
      res_array.append(new_path)
  return res_array

def get_filename_from_path(path):
  filename = path.split('\\')[-1].split('.')[0]
  return filename

def read_image(path : str, grayscale= False):
  if (grayscale):
    img = cv2.imread(path, cv2.IMREAD_GRAYSCALE)
  else:
    img = cv2.imread(path)
    
  if (img is None):
    print("None Image Detected")
  return img

def resize_image(img, scale):
  h, w = img.shape[:2]
  h = int(h * scale)
  w = int(w * scale)
  return cv2.resize(img, (w,h))

def display_image(img, resize_scale=1, title='Display Image'):
  img = resize_image(img, resize_scale)
  cv2.imshow(title, img)
  cv2.waitKey(0)
  cv2.destroyAllWindows()
