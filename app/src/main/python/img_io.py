import cv2
import os


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

  window_width = 1200
  window_height = 720
  scroll_step = 50
  start_x = 0
  start_y = 0

  while True:
      end_x = min(start_x + window_width, img.shape[1])
      end_y = min(start_y + window_height, img.shape[0])
      display_region = img[start_y:end_y, start_x:end_x]

      cv2.imshow(title, display_region)
      key = cv2.waitKey(0)

      # Scroll right
      if key == ord('d'):
          start_x = min(start_x + scroll_step, img.shape[1] - window_width)
      # Scroll left
      elif key == ord('a'):
          start_x = max(start_x - scroll_step, 0)
      # Scroll down
      elif key == ord('s'):
          start_y = min(start_y + scroll_step, img.shape[0] - window_height)
      # Scroll up
      elif key == ord('w'):
          start_y = max(start_y - scroll_step, 0)
      # Exit
      elif key == 27:  # Esc key
          break

  # cv2.imshow(title, img)
  # cv2.waitKey(0)
  cv2.destroyAllWindows()
