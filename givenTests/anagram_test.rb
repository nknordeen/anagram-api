#!/usr/bin/env ruby

require 'json'
require_relative 'anagram_client'
require 'test/unit'

# capture ARGV before TestUnit Autorunner clobbers it

class TestCases < Test::Unit::TestCase

  # runs before each test
  def setup
    @client = AnagramClient.new(ARGV)

    # add words to the dictionary
    @client.post('/words.json', nil, {"words" => ["read", "dear", "dare"] }) rescue nil
  end

  # runs after each test
  def teardown
    # delete everything
    @client.delete('/words.json') rescue nil
  end

  def test_adding_no_words
    res = @client.post('/words.json', nil, {"words" => [] })

    assert_equal('400', res.code, "Unexpected response code")
  end

  def test_adding_words
    res = @client.post('/words.json', nil, {"words" => ["read", "dear", "dare"] })

    assert_equal('201', res.code, "Unexpected response code")
  end

  def test_adding_invalid_words
    res = @client.post('/words.json', nil, {"words" => ["re1"] })

    assert_equal('400', res.code, "Unexpected response code")
  end

  def test_adding_invalid_words_1
    res = @client.post('/words.json', nil, {"words" => ["re/"] })

    assert_equal('400', res.code, "Unexpected response code")
  end

  def test_adding_words_invalid_json
    res = @client.post('/words.json', nil, {"wordss" => ["re"] })

    assert_equal('400', res.code, "Unexpected response code")
  end

  def test_are_anagrams
    res = @client.post('/areAnagrams', nil, {"words" => ["read", "dear", "dare"] })

    assert_equal('200', res.code, "Unexpected response code")
    body = JSON.parse(res.body)

    assert_not_nil(body['areAnagrams'])

    assert_equal(true, body['areAnagrams'])
  end

  def test_are_anagrams_invalid_json
    res = @client.post('/areAnagrams', nil, {"wordss" => ["read", "dear"] })

    assert_equal('400', res.code, "Unexpected response code")

  end

  def test_are_anagrams_invalid_words
    res = @client.post('/areAnagrams', nil, {"words" => ["read", "dear1"] })

    assert_equal('400', res.code, "Unexpected response code")

  end

  def test_fetching_anagrams

    # fetch anagrams
    res = @client.get('/anagrams/read.json')

    assert_equal('200', res.code, "Unexpected response code")
    assert_not_nil(res.body)

    body = JSON.parse(res.body)

    assert_not_nil(body['anagrams'])

    expected_anagrams = %w(dare dear)
    assert_equal(expected_anagrams, body['anagrams'].sort)
  end

  def test_fetching_invalid_anagram

    # fetch anagrams with limit
    res = @client.get('/anagrams/re2ad.json', 'limit=1')

    assert_equal('400', res.code, "Unexpected response code")
  end

  def test_fetching_anagrams_with_limit

    # fetch anagrams with limit
    res = @client.get('/anagrams/read.json', 'limit=1')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)

    assert_equal(1, body['anagrams'].size)
  end

  def test_fetch_for_word_with_no_anagrams

    # fetch anagrams with limit
    res = @client.get('/anagrams/zyxwv.json')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)

    assert_equal(0, body['anagrams'].size)
  end

  def test_deleting_all_words

    res = @client.delete('/words.json')

    assert_equal('204', res.code, "Unexpected response code")

    # should fetch an empty body
    res = @client.get('/anagrams/read.json')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)

    assert_equal(0, body['anagrams'].size)
  end

  def test_deleting_all_words_multiple_times

    3.times do
      res = @client.delete('/words.json')

      assert_equal('204', res.code, "Unexpected response code")
    end

    # should fetch an empty body
    res = @client.get('/anagrams/read.json', 'limit=1')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)

    assert_equal(0, body['anagrams'].size)
  end

  def test_deleting_single_word

    # delete the word
    res = @client.delete('/words/dear.json')

    assert_equal('200', res.code, "Unexpected response code")

    # expect it not to show up in results
    res = @client.get('/anagrams/read.json')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)

    assert_equal(['dare'], body['anagrams'])
  end

  def test_deleting_invalid_word

    # delete the word
    res = @client.delete('/words/dea3r.json')

    assert_equal('400', res.code, "Unexpected response code")
  end

  def test_delete_word_and_anagrams
    
    # verify anagrams are in db
    res_get_anagrams = @client.get('/anagrams/read.json')

    assert_equal('200', res_get_anagrams.code, "Unexpected response code")
    assert_not_nil(res_get_anagrams.body)

    body_anagrams = JSON.parse(res_get_anagrams.body)

    assert_not_nil(body_anagrams['anagrams'])

    expected_anagrams = %w(dare dear)
    assert_equal(expected_anagrams, body_anagrams['anagrams'].sort)

    # delete word and all it's anagrams
    res = @client.delete('/anagrams/dear.json')

    assert_equal('200', res.code, "Unexpected response code")

    # validate that none of the anagrams for this word are here
    res_find_no_anagrams = @client.get('/anagrams/read.json')

    assert_equal('200', res_find_no_anagrams.code, "Unexpected response code")

    body = JSON.parse(res_find_no_anagrams.body)

    assert_equal([], body['anagrams'])
  end

  def test_get_dictionary_stats
    @client.post('/words.json', nil, {"words" => ["nick", "keyboard", "mechanical", "engineering"] }) rescue nil

    res = @client.get('/wordStats')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)
    
    assert_equal(7, body["wordCount"])
    assert_equal(4, body["minWordLength"])
    assert_equal(11, body["maxWordLength"])
    assert_equal(4, body["medianWordLength"])
    assert_equal(6.4286, body["averageWordLength"])
  end

  def test_get_dictionary_stats_empty_db
    # delete everything
    @client.delete('/words.json') rescue nil
    
    res = @client.get('/wordStats')

    assert_equal('200', res.code, "Unexpected response code")

    body = JSON.parse(res.body)
    
    assert_equal(0, body["wordCount"])
    assert_equal(0, body["minWordLength"])
    assert_equal(0, body["maxWordLength"])
    assert_equal(0, body["medianWordLength"])
    assert_equal(0, body["averageWordLength"])
  end
end