# Import from Python File

from img_io import *
from img_process import *
import numpy as np
import os
import cv2

def print_checkpoint(text):
  print(text, " ==  DONE")

def main():
  # Get Images and Border Pattern
  images_path = get_list_image()


  border_path = os.path.join(DATA_PATH, 'border.png')
  border = read_image(border_path)
  # Scale down border since initial picture is too huge
  border = resize_image(border, 0.2)
  assert border is not None, "Border file was not detected"

  # Detecting Border
  # Use first Image as example
  img = images_path[0]
  img = read_image(img)
  display_image(img, 0.5)

  detected_img, border_center_arr = detect_border(img, border)
  assert detected_img is not None, "Border pattern was failed to be detected"
  assert len(border_center_arr) == 4, "Border center is invalid"

  print_checkpoint("Detecting Image Border")

  # Crop Image
  sorted_w = sorted(border_center_arr, key= lambda x: x[0])
  sorted_h = sorted(border_center_arr, key= lambda x: x[1])
  # cropped_img = detected_img[min_h:max_h, min_w: max_w]
  cropped_img = detected_img[int(sorted_h[0][1]): int(sorted_h[3][1]), int(sorted_w[0][0]): int(sorted_w[3][0])]

  print_checkpoint("Cropping Image ")

  display_image(cropped_img, 0.5)


if __name__ == '__main__':
  main()