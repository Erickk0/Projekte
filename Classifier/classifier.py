import math
import sys, os, argparse, json, pickle

"""
  "News Classifier" 
  -------------------------
  This is a small interface for document classification. 
  Implement your own Naive Bayes classifier by completing 
  the class 'NaiveBayesDocumentClassifier' below.

  To run the code, 

  1. place the files 'train.json' and 'test.json' in the current folder.

  2. train your model on 'train.json' by calling > python classifier.py --train 

  3. apply the model to 'test.json' by calling > python classifier.py --apply

"""


class NaiveBayesDocumentClassifier:
    def __init__(self):

        """ The classifier should store all its learned information
            in this 'model' object. Pick whatever form seems appropriate
            to you. Recommendation: use 'pickle' to store/load this model! """

        self.model = None

    def train(self, features, labels):
        self.py = {}
        self.pxy = {}

        # Anzahl documente
        documents_count = len(features)

        doc_count_per_class = {}
        word_presence_per_class = {}

        # Initialisierung
        for label in labels.values():
            if label not in doc_count_per_class:
                doc_count_per_class[label] = 0
                word_presence_per_class[label] = {}

        # Anzahl Dokumente pro Label, Anzahl Dokumente jedes Wort vorkommt
        for doc_id, tokens in features.items():
            label = labels[doc_id]
            doc_count_per_class[label] += 1
            for word in set(tokens):
                if word in word_presence_per_class[label]:
                    word_presence_per_class[label][word] += 1
                else:
                    word_presence_per_class[label][word] = 1

        # P(Y) für jede Klasse
        for label, count in doc_count_per_class.items():
            self.py[label] = count / documents_count

        # P(X|Y)
        for label in word_presence_per_class:
            self.pxy[label] = {}
            class_documents_count = doc_count_per_class[label]
            for word, count in word_presence_per_class[label].items():
                self.pxy[label][word] = count / class_documents_count

        # Speichern der Daten in eine Pickle Datei
        with open('classifier_model.pickle', 'wb') as f:
            pickle.dump({'Py': self.py, 'Pxy': self.pxy}, f)

    def apply(self, features):

        with open('classifier_model.pickle', 'rb') as f:
            model = pickle.load(f)

        py = model['Py']
        pxy = model['Pxy']

        results = {}
        for doc_id, tokens in features.items():
            probabilitys = {}
            for label in py.keys():
                # starten art 0 business 0 ...
                # adding constantly the log of each word probability ,
                # almost same percentage as starting with log (py[art])..

                probabilitys[label] = 0;
                for word in tokens:
                    if word in pxy[label]:
                        # summe der logs von die verschiedene wörter
                        # if  für den wort gefunden = add log pxy
                        # else ??? kann ich mit 0 ? accuracy komplett weg , kein 0
                        # probability wird gelöscht für wörter die nicht gesehen werden

                        probabilitys[label] += math.log(pxy[label][word])
                    else:

                        probabilitys[label] += math.log(1e-10)
                        # wie kann man das anders machen

                for word in pxy[label]:
                    if word not in tokens:
                        # Word is not in the document, use the complement probability
                        probabilitys[label] += math.log(1 - pxy[label][word])
            results[doc_id] = max(probabilitys, key=probabilitys.get)
        return results


def read_json(path):
    with open(path) as f:
        data = json.load(f)['docs']
        features, labels = {}, {}
        for f in data:
            features[f] = data[f]['tokens']
            labels[f] = data[f]['label']
        return features, labels


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='A document classifier.')
    parser.add_argument('--train', help="train the classifier", action='store_true')
    parser.add_argument('--apply', help="apply the classifier (you'll need to train or load a trained model first)",
                        action='store_true')
    args = parser.parse_args()
    classifier = NaiveBayesDocumentClassifier()

    if args.train:
        features, labels = read_json('train.json')
        classifier.train(features, labels)

    if args.apply:
        features, labels = read_json('test.json')
        result = classifier.apply(features)

        # Print results and measure accuracy
        correct = 0
        total = len(features)
        for doc_id, predicted_label in result.items():
            correct_label = labels[doc_id]
            print(f"Title: {doc_id}\nCorrect Label: {correct_label}\nPredicted Label: {predicted_label}\n")
            if predicted_label == correct_label:
                correct += 1
        accuracy = correct / total
        print(f"Accuracy: {accuracy * 100:.2f}%")

        # Print confusion matrix
        confusion_matrix = {}
        for doc_id, predicted_label in result.items():
            correct_label = labels[doc_id]
            if correct_label != predicted_label:
                confusion_matrix[(correct_label, predicted_label)] = confusion_matrix.get(
                    (correct_label, predicted_label), 0) + 1

        print("\nConfusion Matrix (misclassified pairs):")
        for (correct_label, predicted_label), count in confusion_matrix.items():
            print(f"Actual: {correct_label}, Predicted: {predicted_label}, Count: {count}")
