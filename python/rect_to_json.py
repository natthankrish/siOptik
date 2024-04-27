import os
import json

W_REF = 4725
H_REF = 7087
PADDING = 30
TL_BORDER_X = 310
TL_BORDER_Y = 450

NEW_W_REF = W_REF - 2 * TL_BORDER_X + 2 * PADDING
NEW_H_REF = int(1.55 * NEW_W_REF) # 1.55 is ratio after border-cropped

W_RATIO = NEW_W_REF/ W_REF
H_RATIO = NEW_H_REF/ H_REF
SCALE_RATIO = (W_RATIO + H_RATIO) / 2

cwd = os.getcwd()
data_dir = os.path.join(cwd, 'data-out')
metadata_dir = os.path.join(cwd, 'data-metadata')


def print_dict(input_dict, indent):
    for k, v in input_dict.items():
        indentation = "\t" * indent
        if isinstance(v, dict):
            print(indentation, k,":")
            print_dict(v, indent+1)
        elif isinstance(v, list):
            print_list(v, indent+1)
        else:
            print(indentation, k, ":", v)
    print("\n")

def print_list(input_list, indent):
    indentation = "\t" * indent
    print(indentation, "[")
    for elm in input_list:
        if isinstance(elm, dict):
            print_dict(elm, indent+1)
        else:
            print(indentation, elm)
    print(indentation, "]")
        


def get_april_tag_dict():
  april_tag_dict = dict()
  for file_name in os.listdir(metadata_dir):
      file_path = os.path.join(metadata_dir, file_name)
      if (file_name == "apriltag_output.txt"):
          file = open(file_path, "r")
          lines = file.read().split("\n")
          
          for line in lines:
              content = line.split(":")
              original_file_name = content[0].split(".")[0]
              april_tag = int(content[1])
              april_tag_dict[original_file_name] = april_tag

  return april_tag_dict



def get_all_dict(april_tag_dict):
  count = 1
  all_dict = dict()

  all_dict['w_ref'] = NEW_W_REF
  all_dict['h_ref'] = NEW_H_REF

  for file_name in os.listdir(data_dir):
      file_path = os.path.join(data_dir, file_name)

      if os.path.isfile(file_path) and ".txt" in file_path:
          # print(count)
          # print(file_name)
          original_file_name = file_name.split('.')[0]
          april_tag = april_tag_dict[original_file_name]
          all_dict[april_tag] = dict()
          all_dict[april_tag]["original_file_name"] = original_file_name

          # Open File
          file = open(file_path, "r")
          lines = file.read().split("\n")

          all_dict[april_tag]["num_of_boxes"] = int((len(lines) - 1) / 2)
          all_dict[april_tag]["boxes"] = []
          
          id_rect = 1
          for line in lines:
              if "Rect" not in line:
                  
                  content = line.split(' ')
                  temp_dict = dict()

                  if (len(content) == 4):
                    x = int(content[0])
                    y = int(content[1])
                    w = int(content[2]) # Assume w = h

                    adjusted_x = int((int(x) - TL_BORDER_X + PADDING) * W_RATIO)
                    adjusted_y = int((int(y) - TL_BORDER_Y + PADDING) * H_RATIO)
                    adjusted_w = int(int(w) * SCALE_RATIO)

                    temp_dict['id'] = id_rect
                    temp_dict['x'] = adjusted_x
                    temp_dict['y'] = adjusted_y
                    temp_dict['w'] = adjusted_w

                    all_dict[april_tag]["boxes"].append(temp_dict)
                  
                    id_rect +=1

          count += 1
  return all_dict

april_tag_dict = get_april_tag_dict()
# for k, v in april_tag_dict.items():
#     print(k, v)

all_dict = get_all_dict(april_tag_dict)

# print_dict(all_dict, 0)

with open(os.path.join(metadata_dir,'data.json'), 'w') as f:
    json.dump(all_dict, f, indent=4)
